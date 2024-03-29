package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysTenant;

/**
 * 租户数据接口
 *
 * @author tfd
 */
public interface ISysTenantData  extends ICommonData<SysTenant, Long> {

    boolean checkCompanyNameUnique(SysTenant to);

}
