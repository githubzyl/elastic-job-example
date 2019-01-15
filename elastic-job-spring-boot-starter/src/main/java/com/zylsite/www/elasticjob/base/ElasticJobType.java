package com.zylsite.www.elasticjob.base;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public enum ElasticJobType {

	SIMPLE("SIMPLE","简单任务", SimpleJob.class.getSimpleName()),
	DATAFLOW("DATAFLOW","数据流任务", DataflowJob.class.getSimpleName()),
	SCRIPT("SCRIPT","脚本任务", ScriptJob.class.getSimpleName())
	;

	String code;
	String name;
	
	String jobTypeClassName;

	ElasticJobType(String code, String name, String jobTypeClassName) {
		this.code = code;
		this.name = name;
		this.jobTypeClassName = jobTypeClassName;
	}
	
	public static ElasticJobType getByTypeName(String jobTypeClassName) {
		for(ElasticJobType type : ElasticJobType.values()) {
			if(type.getJobTypeClassName().equals(jobTypeClassName)) {
				return type;
			}
		}
		return null;
	}
	
	public String code() {
		return this.code;
	}
	
	public String displayname() {
		return this.name;
	}

	public String getJobTypeClassName() {
		return jobTypeClassName;
	}
	
}
