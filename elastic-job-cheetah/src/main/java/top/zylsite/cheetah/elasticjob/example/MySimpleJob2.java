package top.zylsite.cheetah.elasticjob.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.zylsite.www.elasticjob.annotation.ElasticJob;

@ElasticJob(name = "MySimpleJob2", cron = "0/30 * * * * ?",shardingItemParameters = "0=0,1=1", description = "简单任务", eventTraceRdbDataSource = "datasource")
public class MySimpleJob2 implements SimpleJob {

	public void execute(ShardingContext context) {
		String shardParamter = context.getShardingParameter();
		int value = Integer.parseInt(shardParamter);
		for (int i = 0; i < 1; i++) {
			if (i % 2 == value) {
				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				System.out.println(time + ":[MySimpleJob2]开始执行简单任务2" + i);
			}
		}
	}

}
