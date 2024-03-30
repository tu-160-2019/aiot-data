package com.gitee.starblues.core;

/**
 * 插件关闭类型
 *
 * @author starBlues
 * @since 3.1.0
 * @version 3.1.0
 */
public enum PluginCloseType {

    /**
     * 直接操作停止
     */
    STOP,

    /**
     * 卸载时停止
     */
    UNINSTALL,

    /**
     * 升级时停止
     */
    UPGRADE_UNINSTALL

}
