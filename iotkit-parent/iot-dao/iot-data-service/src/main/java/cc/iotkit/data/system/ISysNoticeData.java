package cc.iotkit.data.system;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.data.ICommonData;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.system.SysNotice;

/**
 * 通知数据接口
 *
 * @author sjg
 */
public interface ISysNoticeData extends ICommonData<SysNotice, Long> {

    /**
     * 按条件分页查询
     */
    Paging<SysNotice> findByConditions(PageRequest<SysNotice> pageRequest);

}
