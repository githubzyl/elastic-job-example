package top.zylsite.cheetah.elasticjob.configuation;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
public class ElasticJobConfiguation {

	/**
    * 任务执行事件数据源
    * @return
    */
   @Bean("datasource")
   @ConfigurationProperties("elasticjob.datasource.druid")
   public DataSource dataSourceTwo(){
       return DruidDataSourceBuilder.create().build();
   }
	
}
