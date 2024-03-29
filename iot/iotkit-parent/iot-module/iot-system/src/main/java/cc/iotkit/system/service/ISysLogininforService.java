package cc.iotkit.system.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.system.dto.bo.SysLoginInfoBo;
import cc.iotkit.system.dto.vo.SysLogininforVo;

import java.util.Collection;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author Lion Li
 */
public interface ISysLogininforService {



    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    void insertLogininfor(SysLoginInfoBo bo);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    List<SysLogininforVo> selectLogininforList(SysLoginInfoBo logininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    void deleteLogininforByIds(Collection<Long> infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();

    Paging<SysLogininforVo> findAll(PageRequest<SysLoginInfoBo> query);
}
