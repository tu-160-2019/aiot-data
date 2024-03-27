package net.srt.flink.common.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ProjectSystemConfiguration
 * @Author zrx
 * @Date 2022/12/26 16:54
 */
public class ProjectSystemConfiguration {
	public static final Map<Long, SystemConfiguration> PROJECT_SYSTEM_CONFIGURATION = new ConcurrentHashMap<>();

	public static SystemConfiguration getByProjectId(Long projectId) {
		if (projectId == null) {
			throw new RuntimeException("projectId is null！");
		}
		//不存在，添加默认配置
		if (!PROJECT_SYSTEM_CONFIGURATION.containsKey(projectId)) {
			PROJECT_SYSTEM_CONFIGURATION.put(projectId, SystemConfiguration.getInstances());
		}
		return PROJECT_SYSTEM_CONFIGURATION.get(projectId);
	}
}
