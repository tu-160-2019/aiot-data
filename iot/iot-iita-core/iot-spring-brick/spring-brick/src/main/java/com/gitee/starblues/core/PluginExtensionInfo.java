package com.gitee.starblues.core;

import java.util.Map;

/**
 * 自主实现插件的扩展信息
 *
 * @author starBlues
 * @version 3.1.0
 * @since 3.1.0
 */
public interface PluginExtensionInfo {

    /**
     * 实现返回扩展信息
     * @return 扩展信息Map
     */
    Map<String, Object> extensionInfo();


}
