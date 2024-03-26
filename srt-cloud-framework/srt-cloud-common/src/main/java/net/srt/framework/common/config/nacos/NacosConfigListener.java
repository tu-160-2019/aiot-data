/*
package net.srt.framework.common.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

*/
/**
 * nacos 配置监听 (不好使，有问题，先注释掉)
 *//*

@Slf4j
@RequiredArgsConstructor
@Component(value = "nacosConfigListener")
public class NacosConfigListener extends AbstractConfigChangeListener implements InitializingBean {

	private final NacosConfigManager nacosConfigManager;
	private final CustomRestartEndpoint restartEndpoint;

	@Override
	public void afterPropertiesSet() throws Exception {
		nacosConfigManager.getConfigService().addListener("datasource.yaml", "DEFAULT_GROUP", this);
	}

	@Override
	public void receiveConfigChange(ConfigChangeEvent event) {
		for (ConfigChangeItem changeItem : event.getChangeItems()) {
			String key = changeItem.getKey();
			String oldValue = changeItem.getOldValue();
			String newValue = changeItem.getNewValue();
			log.warn("change key: " + key);
			log.warn("oldValue: " + oldValue);
			log.warn("newValue: " + newValue);
		}
		//重启服务
		restartEndpoint.restart();
	}
}
*/
