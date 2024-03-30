package com.gitee.starblues.core.descriptor.decrypt;

import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.common.cipher.AbstractPluginCipher;
import com.gitee.starblues.common.cipher.PluginCipher;
import com.gitee.starblues.core.exception.PluginDecryptException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.decrypt.DecryptConfiguration;
import com.gitee.starblues.integration.decrypt.DecryptPluginConfiguration;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PropertiesUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * 默认的 PluginDescriptorDecrypt
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.1
 */
public class DefaultPluginDescriptorDecrypt implements PluginDescriptorDecrypt{

    private final ApplicationContext applicationContext;

    private final DecryptConfiguration decryptConfig;
    private final Map<String, DecryptPluginConfiguration> pluginDecryptConfig;

    public DefaultPluginDescriptorDecrypt(ApplicationContext applicationContext,
                                          IntegrationConfiguration configuration) {
        this.applicationContext = applicationContext;

        this.decryptConfig = configuration.decrypt();
        List<DecryptPluginConfiguration> plugins = decryptConfig.getPlugins();
        if(ObjectUtils.isEmpty(plugins)){
            this.pluginDecryptConfig = Collections.emptyMap();
        } else {
            this.pluginDecryptConfig = new HashMap<>(plugins.size());
            for (DecryptPluginConfiguration plugin : plugins) {
                pluginDecryptConfig.put(plugin.getPluginId(), plugin);
            }
        }
    }

    @Override
    public Properties decrypt(String pluginId, Properties properties) {
        PluginCipher pluginCipher = getPluginCipher(pluginId);
        if(pluginCipher == null){
            return properties;
        }
        try {
            String bootstrapClass = PropertiesUtils.getValue(properties, PluginDescriptorKey.PLUGIN_BOOTSTRAP_CLASS);
            String decrypt = pluginCipher.decrypt(bootstrapClass);
            properties.setProperty(PluginDescriptorKey.PLUGIN_BOOTSTRAP_CLASS, decrypt);
            return properties;
        } catch (Exception e) {
            throw new PluginDecryptException("插件[" + pluginId + "]解密失败. " + e.getMessage());
        }
    }

    protected PluginCipher getPluginCipher(String pluginId){
        if(decryptConfig == null){
            return null;
        }
        Boolean enable = decryptConfig.getEnable();
        if(enable == null || !enable){
            // 没有启用
            return null;
        }
        Map<String, Object> props = decryptConfig.getProps();
        if(props == null){
            props = new HashMap<>();
            decryptConfig.setProps(props);
        }
        String className = decryptConfig.getClassName();
        if(ObjectUtils.isEmpty(pluginDecryptConfig)){
            // 没有配置具体插件的解密配置
            return getPluginCipherBean(className, props);
        }
        DecryptPluginConfiguration decryptPluginConfiguration = pluginDecryptConfig.get(pluginId);
        if(decryptPluginConfiguration == null){
            // 当前插件没有配置解密配置, 说明不启用解密
            return null;
        }
        Map<String, Object> pluginParam = decryptPluginConfiguration.getProps();
        if(!ObjectUtils.isEmpty(pluginParam)){
            props.putAll(pluginParam);
        }
        return getPluginCipherBean(className, props);
    }


    protected PluginCipher getPluginCipherBean(String className, Map<String, Object> params){
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        try {
            if(defaultClassLoader == null){
                defaultClassLoader = this.getClass().getClassLoader();
            }
            Class<?> aClass = defaultClassLoader.loadClass(className);

            String error = "解密实现者[" + className + "]没有继承 [" + AbstractPluginCipher.class.getName() + "]";

            if(aClass.isAssignableFrom(AbstractPluginCipher.class)){
                throw new PluginDecryptException(error);
            }
            Object bean = getBean(aClass);
            if(bean instanceof AbstractPluginCipher){
                AbstractPluginCipher pluginCipher = (AbstractPluginCipher) bean;
                pluginCipher.initParams(params);
                return pluginCipher;
            } else {
                throw new PluginDecryptException(error);
            }
        } catch (ClassNotFoundException e) {
            throw new PluginDecryptException("没有发现解密实现者: " + className);
        }
    }

    protected Object getBean(Class<?> aClass){
        try {
            return applicationContext.getBean(aClass);
        } catch (Exception e1){
            try {
                return aClass.getConstructor().newInstance();
            } catch (Exception e2){
                throw new PluginDecryptException(e2);
            }
        }
    }

}
