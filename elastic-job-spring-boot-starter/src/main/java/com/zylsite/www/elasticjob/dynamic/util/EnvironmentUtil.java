package com.zylsite.www.elasticjob.dynamic.util;

import org.springframework.core.env.Environment;

/**
 * <p>Description: 获取环境变量的值</p>
 * @author   zhaoyl
 * @date      2019-01-10
 * @version  v1.0
 */
public class EnvironmentUtil {

   protected static Environment env = SpringBeanUtil.getBean(Environment.class);
	
	public  static String getProperty(String key) {
		return env.getProperty(key);
	}
	
	public  static String getProperty(String key, String defaultValue) {
		return null == getProperty(key) ? defaultValue : getProperty(key);
	}
	
	public static Long getLongProperty(String key) {
		try {
			return Long.parseLong(getProperty(key));
		}catch(Exception e) {
			return null;
		}
	}
	
	public static  Long getLongProperty(String key, long defaultValue) {
		return null == getLongProperty(key) ? defaultValue : getLongProperty(key);
	}
	
	public static Integer getIntProperty(String key) {
		try {
			return Integer.parseInt(getProperty(key));
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Integer getIntProperty(String key, int defaultValue) {
		return null == getIntProperty(key) ? defaultValue : getIntProperty(key);
	}
	
	public static Boolean getBooleanProperty(String key) {
		try {
			return Boolean.parseBoolean(getProperty(key));
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Boolean getBooleanProperty(String key, boolean defaultValue) {
		return null == getBooleanProperty(key) ? defaultValue : getBooleanProperty(key);
	}
	
}
