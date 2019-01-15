package com.zylsite.www.elasticjob.dynamic.bean;

import com.zylsite.www.elasticjob.annotation.ElasticJob;
import com.zylsite.www.elasticjob.base.ElasticJobAttribute;
import com.zylsite.www.elasticjob.base.ElasticJobType;
import com.zylsite.www.elasticjob.dynamic.util.JobInitUtil;

public class JobBuilder {

	public static Job build(String jobClass, String jobTypeName, ElasticJob conf) {
		String jobName = conf.name();
		Job job = new Job();
		job.setJobType(ElasticJobType.getByTypeName(jobTypeName));
		job.setJobName(jobName);
		job.setJobClass(jobClass);
		job.setCron(JobInitUtil.getEnvironmentStringValue(jobName, ElasticJobAttribute.CRON, conf.cron()));
		job.setShardingItemParameters(JobInitUtil.getEnvironmentStringValue(jobName,
				ElasticJobAttribute.SHARDING_ITEM_PARAMETERS, conf.shardingItemParameters()));
		job.setDescription(
				JobInitUtil.getEnvironmentStringValue(jobName, ElasticJobAttribute.DESCRIPTION, conf.description()));
		job.setJobParameter(
				JobInitUtil.getEnvironmentStringValue(jobName, ElasticJobAttribute.JOB_PARAMETER, conf.jobParameter()));
		job.setJobShardingStrategyClass(JobInitUtil.getEnvironmentStringValue(jobName,
				ElasticJobAttribute.JOB_SHARDING_STRATEGY_CLASS, conf.jobShardingStrategyClass()));
		job.setEventTraceRdbDataSource(JobInitUtil.getEnvironmentStringValue(jobName,
				ElasticJobAttribute.EVENT_TRACE_RDB_DATA_SOURCE, conf.eventTraceRdbDataSource()));
		job.setScriptCommandLine(JobInitUtil.getEnvironmentStringValue(jobName, ElasticJobAttribute.SCRIPT_COMMAND_LINE,
				conf.scriptCommandLine()));
		job.setFailover(JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.FAILOVER, conf.failover()));
		job.setMisfire(JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.MISFIRE, conf.misfire()));
		job.setOverwrite(
				JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.OVERWRITE, conf.overwrite()));
		job.setDisabled(JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.DISABLED, conf.disabled()));
		job.setMonitorExecution(JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.MONITOR_EXECUTION,
				conf.monitorExecution()));
		job.setStreamingProcess(JobInitUtil.getEnvironmentBooleanValue(jobName, ElasticJobAttribute.STREAMING_PROCESS,
				conf.streamingProcess()));
		job.setShardingTotalCount(JobInitUtil.getEnvironmentIntValue(jobName, ElasticJobAttribute.SHARDING_TOTAL_COUNT,
				conf.shardingTotalCount()));
		job.setMonitorPort(
				JobInitUtil.getEnvironmentIntValue(jobName, ElasticJobAttribute.MONITOR_PORT, conf.monitorPort()));
		job.setMaxTimeDiffSeconds(JobInitUtil.getEnvironmentIntValue(jobName, ElasticJobAttribute.MAX_TIME_DIFF_SECONDS,
				conf.maxTimeDiffSeconds()));
		job.setReconcileIntervalMinutes(JobInitUtil.getEnvironmentIntValue(jobName,
				ElasticJobAttribute.RECONCILE_INTERVAL_MINUTES, conf.reconcileIntervalMinutes()));
		JobProperties properties = new JobProperties();
		properties.setJobExceptionHandler(JobInitUtil.getEnvironmentStringValue(jobName,
				ElasticJobAttribute.JOB_EXCEPTION_HANDLER, conf.jobExceptionHandler()));
		properties.setExecutorServiceHandler(JobInitUtil.getEnvironmentStringValue(jobName,
				ElasticJobAttribute.EXECUTOR_SERVICE_HANDLER, conf.executorServiceHandler()));
		job.setJobProperties(properties);
		return job;
	}

}
