package com.zylsite.www.elasticjob.dynamic.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zylsite.www.elasticjob.dynamic.bean.Job;
import com.zylsite.www.elasticjob.dynamic.service.JobService;
import com.zylsite.www.elasticjob.dynamic.util.JobInitUtil;
import com.zylsite.www.elasticjob.dynamic.util.JsonUtils;

@Service
public class JobServiceImpl implements JobService {

	private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	private ZookeeperRegistryCenter zookeeperRegistryCenter;

	@Autowired
	private ApplicationContext ctx;

	// 记录任务添加次数
	private Map<String, AtomicInteger> JOB_ADD_COUNT = new ConcurrentHashMap<String, AtomicInteger>();

	@Override
	public void addJob(Job job) {
		JobInitUtil.addJobToElasticJob(job, ctx);
		logger.info("【" + job.getJobName() + "】\t" + job.getJobClass() + "\tinit success");
	}

	@Override
	public void removeJob(String jobName) throws Exception {
		CuratorFramework client = zookeeperRegistryCenter.getClient();
		client.delete().deletingChildrenIfNeeded().forPath("/" + jobName);
	}

	@Autowired
	@SuppressWarnings("resource")
	public void monitorJobRegister() {
		CuratorFramework client = zookeeperRegistryCenter.getClient();
		PathChildrenCache childrenCache = new PathChildrenCache(client, "/", true);
		PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				ChildData data = event.getData();
				switch (event.getType()) {
				case CHILD_ADDED:
					String config = new String(client.getData().forPath(data.getPath() + "/config"));
					Job job = JsonUtils.toBean(Job.class, config);
					// 启动时任务会添加数据触发事件，这边需要去掉第一次的触发，不然在控制台进行手动触发任务会执行两次任务
					if (!JOB_ADD_COUNT.containsKey(job.getJobName())) {
						JOB_ADD_COUNT.put(job.getJobName(), new AtomicInteger());
					}
					int count = JOB_ADD_COUNT.get(job.getJobName()).incrementAndGet();
					if (count > 1) {
						addJob(job);
					}
					break;
				default:
					break;
				}
			}
		};
		childrenCache.getListenable().addListener(childrenCacheListener);
		try {
			childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
