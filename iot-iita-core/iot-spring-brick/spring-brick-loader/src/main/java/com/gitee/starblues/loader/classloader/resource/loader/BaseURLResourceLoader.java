package com.gitee.starblues.loader.classloader.resource.loader;

import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;

import java.net.URL;

/**
 * 基本 URL 资源加载
 *
 * @author starBlues
 * @since 3.1.2
 * @version 3.1.2
 */
public class BaseURLResourceLoader implements ResourceLoader{

    private final URL baseUrl;

    public BaseURLResourceLoader(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void load(ResourceStorage resourceStorage) throws Exception {

    }

    @Override
    public void close() throws Exception {

    }
}
