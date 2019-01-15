package com.zylsite.www.elasticjob.dynamic.util;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zylsite.www.elasticjob.annotation.ElasticJob;
import com.zylsite.www.elasticjob.base.ElasticJobAttribute;
import com.zylsite.www.elasticjob.base.ElasticJobType;
import com.zylsite.www.elasticjob.dynamic.bean.Job;

public class JobInitUtil {

	public final static String ELASTIC_JOB_PREFIX = "elasticjob.";
	public final static String SPRING_JOB_SCHEDULER = "SpringJobScheduler";

	public static void addJobToElasticJob(Job job, ApplicationContext ctx) {
		// 核心配置
		JobCoreConfiguration coreConfig = JobInitUtil.buildCoreConfiguration(job);
		// 不同类型的任务配置处理
		ElasticJobType jobType = job.getJobType();
		JobTypeConfiguration typeConfig = JobInitUtil.getJobTypeConfiguration(job, jobType, coreConfig);
		LiteJobConfiguration jobConfig = JobInitUtil.buildJobConfiguration(job, typeConfig);
		List<BeanDefinition> elasticJobListeners = JobInitUtil.getTargetElasticJobListeners(job);
		// 构建SpringJobScheduler对象来初始化任务
		SpringJobScheduler springJobScheduler = JobInitUtil.buildSpringJobScheduler(jobType, jobConfig,
				elasticJobListeners, job, ctx);
		springJobScheduler.init();
	}

	// 构建核心配置
	public static JobCoreConfiguration buildCoreConfiguration(Job job) {
		return JobCoreConfiguration.newBuilder(job.getJobName(), job.getCron(), job.getShardingTotalCount())
				.shardingItemParameters(job.getShardingItemParameters()).description(job.getDescription())
				.failover(job.isFailover()).jobParameter(job.getJobParameter()).misfire(job.isMisfire())
				.jobProperties(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
						job.getJobProperties().getJobExceptionHandler())
				.jobProperties(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
						job.getJobProperties().getExecutorServiceHandler())
				.build();
	}

	public static LiteJobConfiguration buildJobConfiguration(Job job, JobTypeConfiguration typeConfig) {
		return LiteJobConfiguration.newBuilder(typeConfig).overwrite(job.isOverwrite()).disabled(job.isDisabled())
				.monitorPort(job.getMonitorPort()).monitorExecution(job.isMonitorExecution())
				.maxTimeDiffSeconds(job.getMaxTimeDiffSeconds())
				.jobShardingStrategyClass(job.getJobShardingStrategyClass())
				.reconcileIntervalMinutes(job.getReconcileIntervalMinutes()).build();
	}

	public static JobTypeConfiguration getJobTypeConfiguration(Job job, ElasticJobType jobType,
			JobCoreConfiguration coreConfig) {
		if (ElasticJobType.SIMPLE == jobType) {
			return new SimpleJobConfiguration(coreConfig, job.getJobClass());
		} else if (ElasticJobType.DATAFLOW == jobType) {
			return new DataflowJobConfiguration(coreConfig, job.getJobClass(), job.isStreamingProcess());
		} else if (ElasticJobType.SCRIPT == jobType) {
			return new ScriptJobConfiguration(coreConfig, job.getScriptCommandLine());
		} else {
			return null;
		}
	}

	public static SpringJobScheduler buildSpringJobScheduler(ElasticJobType jobType, LiteJobConfiguration jobConfig,
			List<BeanDefinition> elasticJobListeners, Job job, ApplicationContext ctx) {
		ZookeeperRegistryCenter zookeeperRegistryCenter = ctx.getBean(ZookeeperRegistryCenter.class);
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
		factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		if (ElasticJobType.SCRIPT == jobType) {
			factory.addConstructorArgValue(null);
		} else {
			BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(job.getJobClass());
			factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
		}
		factory.addConstructorArgValue(zookeeperRegistryCenter);
		factory.addConstructorArgValue(jobConfig);
		// 任务执行日志数据源，以名称获取
		if (StringUtils.hasText(job.getEventTraceRdbDataSource())) {
			BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
			rdbFactory.addConstructorArgReference(job.getEventTraceRdbDataSource());
			factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
		}
		factory.addConstructorArgValue(elasticJobListeners);
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx
				.getAutowireCapableBeanFactory();
		defaultListableBeanFactory.registerBeanDefinition(SPRING_JOB_SCHEDULER, factory.getBeanDefinition());
		SpringJobScheduler springJobScheduler = (SpringJobScheduler) ctx.getBean(SPRING_JOB_SCHEDULER);
		return springJobScheduler;
	}

	public static List<BeanDefinition> getTargetElasticJobListeners(Job job) {
		List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
		String listeners = job.getListener();
		if (StringUtils.hasText(listeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			result.add(factory.getBeanDefinition());
		}

		String distributedListeners = job.getDistributedListener();
		long startedTimeoutMilliseconds = job.getStartedTimeoutMilliseconds();
		long completedTimeoutMilliseconds = job.getCompletedTimeoutMilliseconds();

		if (StringUtils.hasText(distributedListeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			factory.addConstructorArgValue(startedTimeoutMilliseconds);
			factory.addConstructorArgValue(completedTimeoutMilliseconds);
			result.add(factory.getBeanDefinition());
		}
		return result;
	}

	public static List<BeanDefinition> getTargetElasticJobListeners(ElasticJob conf) {
		List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
		String listeners = getEnvironmentStringValue(conf.name(), ElasticJobAttribute.LISTENER, conf.listener());
		if (StringUtils.hasText(listeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			result.add(factory.getBeanDefinition());
		}

		String distributedListeners = getEnvironmentStringValue(conf.name(), ElasticJobAttribute.DISTRIBUTED_LISTENER,
				conf.distributedListener());
		long startedTimeoutMilliseconds = getEnvironmentLongValue(conf.name(),
				ElasticJobAttribute.DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS,
				conf.startedTimeoutMilliseconds());
		long completedTimeoutMilliseconds = getEnvironmentLongValue(conf.name(),
				ElasticJobAttribute.DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS,
				conf.completedTimeoutMilliseconds());

		if (StringUtils.hasText(distributedListeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			factory.addConstructorArgValue(startedTimeoutMilliseconds);
			factory.addConstructorArgValue(completedTimeoutMilliseconds);
			result.add(factory.getBeanDefinition());
		}
		return result;
	}

	public static String getEnvironmentStringValue(String jobName, String fieldName, String defaultValue) {
		return EnvironmentUtil.getProperty(getKey(jobName, fieldName), defaultValue);
	}

	public static int getEnvironmentIntValue(String jobName, String fieldName, int defaultValue) {
		return EnvironmentUtil.getIntProperty(getKey(jobName, fieldName), defaultValue);
	}

	public static long getEnvironmentLongValue(String jobName, String fieldName, long defaultValue) {
		return EnvironmentUtil.getLongProperty(getKey(jobName, fieldName), defaultValue);
	}

	public static boolean getEnvironmentBooleanValue(String jobName, String fieldName, boolean defaultValue) {
		return EnvironmentUtil.getBooleanProperty(getKey(jobName, fieldName), defaultValue);
	}

	public static String getKey(String jobName, String fieldName) {
		return ELASTIC_JOB_PREFIX + jobName + "." + fieldName;
	}

}
