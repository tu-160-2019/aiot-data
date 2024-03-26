package net.srt.framework.common.cache;

/**
 * Redis Key管理
 *
 * @author 阿沐 babamu@126.com
 */
public class RedisKeys {

	/**
	 * 验证码Key
	 */
	public static String getCaptchaKey(String key) {
		return "sys:captcha:" + key;
	}

	/**
	 * accessToken Key
	 */
	public static String getAccessTokenKey(String accessToken) {
		return "sys:access:" + accessToken;
	}

	/**
	 * accessToken Key
	 */
	public static String getProjectIdKey(String accessToken) {
		return "sys:project:id:" + accessToken;
	}

	/**
	 * projectId Key
	 */
	public static String getProjectKey(Long projectId) {
		return "sys:project:" + projectId;
	}

	/**
	 * appToken Key
	 */
	public static String getAppTokenKey(String appToken) {
		return "app:token:" + appToken;
	}

	/**
	 * getAppIdKey
	 */
	public static String getAppIdKey(Long appId) {
		return "app:id:" + appId;
	}


	/**
	 * getNeo4jKey
	 */
	public static String getNeo4jKey(Long projectId) {
		return "neo4j:info:" + projectId;
	}

	public static String getConsoleLogKey(String accessToken) {
		return "console:log:" + accessToken;
	}

	public static String getProcessKey(String key) {
		return "develop:process:" + key;
	}
}
