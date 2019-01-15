package top.zylsite.cheetah.elasticjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.zylsite.www.elasticjob.annotation.EnableElasticJob;

@EnableElasticJob
@SpringBootApplication
public class ElasticJobCheetahApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticJobCheetahApplication.class, args);
	}

}

