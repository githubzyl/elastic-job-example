package com.zylsite.www.elasticjob.dynamic.service;

import com.zylsite.www.elasticjob.dynamic.bean.Job;

public interface JobService {
	
	/**
	 * 增加任务
	 */
	public void addJob(Job job);
	
	/**
	 * 删除任务
	 */
	public void removeJob(String jobName) throws Exception;
	
	/**
	 * 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
	 */
	public void monitorJobRegister();
	
}
