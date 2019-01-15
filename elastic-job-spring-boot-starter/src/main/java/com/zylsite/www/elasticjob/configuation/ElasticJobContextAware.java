package com.zylsite.www.elasticjob.configuation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import com.zylsite.www.elasticjob.annotation.ElasticJob;
import com.zylsite.www.elasticjob.dynamic.bean.Job;
import com.zylsite.www.elasticjob.dynamic.bean.JobBuilder;
import com.zylsite.www.elasticjob.dynamic.service.JobService;
import com.zylsite.www.elasticjob.dynamic.util.JobInitUtil;
import com.zylsite.www.elasticjob.dynamic.util.SpringBeanUtil;

@Configuration
public class ElasticJobContextAware implements ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(ElasticJobContextAware.class);

	@Autowired(required = false)
	private JobService jobService;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		SpringBeanUtil.setApplicationContext(ctx);
		
		Map<String, Object> beanMap = ctx.getBeansWithAnnotation(ElasticJob.class);
		for (Object confBean : beanMap.values()) {
			Class<?> clz = confBean.getClass();
			String jobClass = clz.getName();
			String jobTypeName = confBean.getClass().getInterfaces()[0].getSimpleName();
			ElasticJob conf = clz.getAnnotation(ElasticJob.class);
			Job job = JobBuilder.build(jobClass, jobTypeName,conf);
			JobInitUtil.addJobToElasticJob(job, ctx);
			logger.info("【" + job.getJobName() + "】\t" + jobClass + "\tinit success");
		}

		// 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
		if (jobService != null) {
			jobService.monitorJobRegister();
		}

	}

}
