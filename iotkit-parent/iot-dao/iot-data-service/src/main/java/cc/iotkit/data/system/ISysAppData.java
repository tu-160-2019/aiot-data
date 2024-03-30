package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysApp;

/**
 * 数据接口
 *
 * @author Lion Li
 * @date 2023-08-10
 */
public interface ISysAppData extends ICommonData<SysApp, Long> {

    SysApp findByAppId(String appId);

}
