package net.srt.framework.security.cache;

import lombok.AllArgsConstructor;
import net.srt.framework.common.cache.RedisCache;
import net.srt.framework.common.cache.RedisKeys;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.cache.bean.Neo4jInfo;
import net.srt.framework.common.utils.JsonUtils;
import net.srt.framework.security.user.UserDetail;
import org.springframework.stereotype.Component;

/**
 * 认证 Cache
 *
 * @author 阿沐 babamu@126.com
 */
@Component
@AllArgsConstructor
public class TokenStoreCache {
	private final RedisCache redisCache;

	public boolean containsKey(String key) {
		return redisCache.hasKey(key);
	}

	public void saveUser(String accessToken, UserDetail user) {
		String key = RedisKeys.getAccessTokenKey(accessToken);
		redisCache.set(key, user);
	}

	public UserDetail getUser(String accessToken) {
		String key = RedisKeys.getAccessTokenKey(accessToken);
		return (UserDetail) redisCache.get(key);
	}

	public void deleteUser(String accessToken) {
		String key = RedisKeys.getAccessTokenKey(accessToken);
		redisCache.delete(key);
	}

	public void saveProjectId(String accessToken, Long projectId) {
		String key = RedisKeys.getProjectIdKey(accessToken);
		redisCache.set(key, projectId);
	}

	public Long getProjectId(String accessToken) {
		String key = RedisKeys.getProjectIdKey(accessToken);
		Object projectId = redisCache.get(key);
		if (projectId == null) {
			return null;
		}
		return Long.parseLong(projectId.toString());
	}

	public void saveProject(Long projectId, DataProjectCacheBean projectCacheBean) {
		String key = RedisKeys.getProjectKey(projectId);
		redisCache.set(key, JsonUtils.toJsonString(projectCacheBean), RedisCache.NOT_EXPIRE);
	}

	public DataProjectCacheBean getProject(Long projectId) {
		String key = RedisKeys.getProjectKey(projectId);
		String projectJson = (String) redisCache.get(key);
		if (projectJson == null) {
			return null;
		}
		return JsonUtils.parseObject(projectJson, DataProjectCacheBean.class);
	}

	public void deleteProject(Long projectId) {
		String key = RedisKeys.getProjectKey(projectId);
		redisCache.delete(key);
	}

	public void saveNeo4jInfo(Long projectId, Neo4jInfo neo4jInfo) {
		String key = RedisKeys.getNeo4jKey(projectId);
		redisCache.set(key, JsonUtils.toJsonString(neo4jInfo), RedisCache.NOT_EXPIRE);
	}

	public Neo4jInfo getNeo4jInfo(Long projectId) {
		String key = RedisKeys.getNeo4jKey(projectId);
		String projectJson = (String) redisCache.get(key);
		if (projectJson == null) {
			return null;
		}
		return JsonUtils.parseObject(projectJson, Neo4jInfo.class);
	}


	public void saveCconsoleLog(String accessToken, String log) {
		String key = RedisKeys.getConsoleLogKey(accessToken);
		redisCache.set(key, log, RedisCache.HOUR_ONE_EXPIRE);
	}

	public String getConsoleLog(String accessToken) {
		String key = RedisKeys.getConsoleLogKey(accessToken);
		return (String) redisCache.get(key);
	}

	public void deleteConsoleLog(String accessToken) {
		String key = RedisKeys.getConsoleLogKey(accessToken);
		redisCache.delete(key);
	}

	public void saveProcess(String key, String process) {
		String processKey = RedisKeys.getProcessKey(key);
		redisCache.set(processKey, process);
	}

	public void deleteProcess(String key) {
		String processKey = RedisKeys.getProcessKey(key);
		redisCache.delete(processKey);
	}

	public String getProcess(String key) {
		String processKey = RedisKeys.getProcessKey(key);
		return (String) redisCache.get(processKey);
	}
}
