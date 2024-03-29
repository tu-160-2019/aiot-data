package cc.iotkit.plugin.core;


import java.util.Map;

/**
 * 插件接口
 *
 * @author sjg
 */
public interface IPlugin {

    /**
     * 获取设备连接信息，如连接mqtt的ip、端口、账号、密码。。。
     *
     * @param pk 产品key
     * @param dn 设备dn
     * @return 连接配置项
     */
    Map<String, Object> getLinkInfo(String pk, String dn);

}
