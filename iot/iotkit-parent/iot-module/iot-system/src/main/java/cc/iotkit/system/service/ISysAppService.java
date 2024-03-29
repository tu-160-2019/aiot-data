package cc.iotkit.system.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.system.dto.bo.SysAppBo;
import cc.iotkit.system.dto.vo.SysAppVo;

import java.util.List;

/**
 * 应用信息Service接口
 *
 * @author tfd
 * @date 2023-08-10
 */
public interface ISysAppService {

    /**
     * 查询应用信息
     */
    SysAppVo queryById(Long id);

    /**
     * 根据appid查询应用信息
     */
    SysAppVo queryByAppId(String appId);

    /**
     * 根据appid查询应用信息是否存在
     */
    boolean checkAppIdUnique(String appId);

    /**
     * 查询应用信息列表
     */
    Paging<SysAppVo> queryPageList(PageRequest<SysAppBo> pageQuery);

    /**
     * 查询应用信息列表
     */
    List<SysAppVo> queryList(SysAppBo bo);

    /**
     * 新增应用信息
     */
    Long insertByBo(SysAppBo bo);

    /**
     * 修改应用信息
     */
    Boolean updateByBo(SysAppBo bo);

    /**
     * 删除应用信息信息
     */
    Boolean deleteById(Long id);
}
