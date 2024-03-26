package net.srt.system.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.framework.security.user.UserDetail;
import net.srt.system.entity.SysUserEntity;
import net.srt.system.query.SysRoleUserQuery;
import net.srt.system.query.SysUserQuery;
import net.srt.system.vo.SysUserVO;

import java.util.List;

/**
 * 用户管理
 *
 * @author 阿沐 babamu@126.com
 */
public interface SysUserService extends BaseService<SysUserEntity> {

    PageResult<SysUserVO> page(SysUserQuery query);

    void save(SysUserVO vo);

    void update(SysUserVO vo);

    void delete(List<Long> idList);

    SysUserVO getByMobile(String mobile);

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    void updatePassword(Long id, String newPassword);

    /**
     * 分配角色，用户列表
     */
    PageResult<SysUserVO> roleUserPage(SysRoleUserQuery query);

	PageResult<SysUserVO> pageProject(SysUserQuery query);

	void deleteProject(Long projectId, List<Long> idList);

	List<Long> getProjectIds(UserDetail userDetail);

	List<SysUserVO> listAll();

	List<SysUserVO> listUsers();
}
