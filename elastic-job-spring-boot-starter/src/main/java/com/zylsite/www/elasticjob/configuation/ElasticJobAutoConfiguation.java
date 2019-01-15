package com.zylsite.www.elasticjob.configuation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zylsite.www.elasticjob.base.ZookeeperProperties;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ElasticJobAutoConfiguation {

	@Autowired
	private ZookeeperProperties zookeeperProperties;
	
	/**
	 * 初始化Zookeeper注册中心
	 * @param config
	 * @return
	 */
	@Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter() {
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(zookeeperProperties.getServerList(), 
				zookeeperProperties.getNamespace());
		zkConfig.setBaseSleepTimeMilliseconds(zookeeperProperties.getBaseSleepTimeMilliseconds());
		zkConfig.setConnectionTimeoutMilliseconds(zookeeperProperties.getConnectionTimeoutMilliseconds());
		zkConfig.setDigest(zookeeperProperties.getDigest());
		zkConfig.setMaxRetries(zookeeperProperties.getMaxRetries());
		zkConfig.setMaxSleepTimeMilliseconds(zookeeperProperties.getMaxSleepTimeMilliseconds());
		zkConfig.setSessionTimeoutMilliseconds(zookeeperProperties.getSessionTimeoutMilliseconds());
		return new ZookeeperRegistryCenter(zkConfig);
    }
	
	@Bean
	public ElasticJobContextAware elasticJobContextAware() {
		return new ElasticJobContextAware();
	}
	
}
