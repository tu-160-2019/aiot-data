package com.gitee.starblues.loader.utils;

/**
 * 可释放资源接口
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 */
public interface Release {

    /**
     * 释放资源
     * @throws Exception 释放异常
     */
    default void release() throws Exception{}

}
