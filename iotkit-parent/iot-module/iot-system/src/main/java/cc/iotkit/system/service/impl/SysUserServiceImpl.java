package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.service.UserService;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.data.system.*;
import cc.iotkit.model.system.SysRole;
import cc.iotkit.model.system.SysUser;
import cc.iotkit.model.system.SysUserPost;
import cc.iotkit.model.system.SysUserRole;
import cc.iotkit.system.dto.bo.SysUserBo;
import cc.iotkit.system.dto.vo.SysUserVo;
import cc.iotkit.system.service.ISysUserService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements ISysUserService, UserService {

    @Autowired
    private ISysUserData sysUserData;

    @Autowired
    private ISysDeptData sysDeptData;

    @Autowired
    private ISysRoleData sysRoleData;

    @Autowired
    private ISysPostData sysPostData;

    @Autowired
    private ISysUserRoleData sysUserRoleData;

    @Autowired
    private ISysUserPostData sysUserPostData;


    @Override
    public String selectUserNameById(Long userId) {
        return null;
    }

    @Override
    public Paging<SysUserVo> selectPageUserList(PageRequest<SysUserBo> query) {
        return sysUserData.findAll(query.to(SysUser.class)).to(SysUserVo.class);
    }

    @Override
    public List<SysUserVo> selectUserList(SysUserBo user) {
        return MapstructUtils.convert(sysUserData.findAllByCondition(user.to(SysUser.class)), SysUserVo.class);
    }

    @Override
    public Paging<SysUserVo> selectAllocatedList(PageRequest<SysUserBo> query) {
        return sysUserData.selectAllocatedList(query.to(SysUser.class)).to(SysUserVo.class);
    }

    @Override
    public Paging<SysUserVo> selectUnallocatedList(PageRequest<SysUserBo> query) {
        return sysUserData.selectUnallocatedList(query.to(SysUser.class)).to(SysUserVo.class);
    }

    @Override
    public SysUserVo selectUserByUserName(String userName) {
        return MapstructUtils.convert(sysUserData.selectUserByUserName(userName), SysUserVo.class);
    }

    @Override
    public SysUserVo selectUserByPhonenumber(String phonenumber) {
        return MapstructUtils.convert(sysUserData.findByPhonenumber(phonenumber), SysUserVo.class);

    }

    @Override
    public SysUserVo selectUserById(Long userId) {
        return MapstructUtils.convert(sysUserData.findById(userId), SysUserVo.class);
    }

    @Override
    public String selectUserRoleGroup(String userName) {
        return sysUserData.selectUserRoleGroup(userName);
    }

    @Override
    public String selectUserPostGroup(String userName) {
        return sysUserData.selectUserPostGroup(userName);
    }

    @Override
    public boolean checkUserNameUnique(SysUserBo user) {
        return sysUserData.checkUserNameUnique(user.to(SysUser.class));
    }

    @Override
    public boolean checkPhoneUnique(SysUserBo user) {
        return sysUserData.checkPhoneUnique(user.to(SysUser.class));
    }

    @Override
    public boolean checkEmailUnique(SysUserBo user) {
        return sysUserData.checkEmailUnique(user.to(SysUser.class));
    }

    @Override
    public void checkUserAllowed(SysUserBo user) {
        if (ObjectUtil.isNotNull(user.getId()) && user.isSuperAdmin()) {
            throw new BizException(ErrCode.UNAUTHORIZED_EXCEPTION);
        }
    }

    @Override
    public void checkUserDataScope(Long userId) {
        if (ObjectUtil.isNull(userId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (ObjectUtil.isNull(sysUserData.findById(userId))) {
            throw new BizException(ErrCode.UNAUTHORIZED_EXCEPTION);
        }
    }

    @Override
    public int insertUser(SysUserBo user) {
        // 新增用户信息
        SysUser newUser = sysUserData.save(user.to(SysUser.class));
        user.setId(newUser.getId());
        // 新增用户岗位关联
        insertUserPost(user, false);
        // 新增用户与角色管理
        insertUserRole(user, false);
        return newUser != null ? 1 : 0;
    }

    /**
     * 新增用户角色信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserRole(SysUserBo user, boolean clear) {
        this.insertUserRole(user.getId(), user.getRoleIds(), clear);
    }

    /**
     * 新增用户岗位信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserPost(SysUserBo user, boolean clear) {
        List<Long> posts = user.getPostIds();
        if (CollectionUtil.isNotEmpty(posts)) {
            if (clear) {
                // 删除用户与岗位关联
                sysUserPostData.deleteByUserId(user.getId());
            }
            // 新增用户与岗位管理
            List<SysUserPost> list = StreamUtils.toList(posts, postId -> {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getId());
                up.setPostId(postId);
                return up;
            });
            sysUserPostData.batchSave(list);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private void insertUserRole(Long userId, List<Long> roleIds, boolean clear) {
        if (CollectionUtil.isNotEmpty(roleIds)) {
            // 判断是否具有此角色的操作权限
            List<SysRole> roles = sysRoleData.selectRoleList(new SysRole());
            if (CollUtil.isEmpty(roles)) {
                throw new BizException(ErrCode.UNAUTHORIZED_EXCEPTION);
            }
            List<Long> roleList = StreamUtils.toList(roles, SysRole::getId);
            if (!LoginHelper.isSuperAdmin(userId)) {
                roleList.remove(UserConstants.SUPER_ADMIN_ID);
            }
            List<Long> canDoRoleList = StreamUtils.filter(roleIds, roleList::contains);
            if (CollUtil.isEmpty(canDoRoleList)) {
                throw new BizException(ErrCode.UNAUTHORIZED_EXCEPTION);
            }
            if (clear) {
                // 删除用户与角色关联
                sysUserRoleData.deleteByUserId(userId);
            }
            // 新增用户与角色管理
            List<SysUserRole> list = StreamUtils.toList(canDoRoleList, roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            });
            sysUserRoleData.batchSave(list);
        }
    }

    @Override
    public boolean registerUser(SysUserBo user, String tenantId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUserBo user) {
        // 新增用户与角色管理
        insertUserRole(user, true);
        // 新增用户与岗位管理
        insertUserPost(user, true);
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        // 防止错误更新后导致的数据误删除
        SysUser ret = sysUserData.save(sysUser);
        if (ret == null) {
            throw new BizException("修改用户" + user.getUserName() + "信息失败");
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, List<Long> roleIds) {
        insertUserRole(userId, roleIds, true);
    }

    @Override
    public int updateUserStatus(Long userId, String status) {
        SysUser user = sysUserData.findById(userId);
        user.setStatus(status);
        return sysUserData.save(user) != null ? 1 : 0;
    }

    @Override
    public int updateUserProfile(SysUserBo user) {
        SysUser oldUser = sysUserData.findById(user.getId());
        if (ObjectUtil.isNotNull(user.getNickName())) {
            oldUser.setNickName(user.getNickName());
        }
        oldUser.setPhonenumber(user.getPhonenumber());
        oldUser.setEmail(user.getEmail());
        oldUser.setSex(user.getSex());
        return sysUserData.save(oldUser) != null ? 1 : 0;
    }

    @Override
    public boolean updateUserAvatar(Long userId, Long avatar) {
        SysUser oldUser = sysUserData.findById(userId);
        oldUser.setAvatar(avatar);
        sysUserData.save(oldUser);
        return Boolean.TRUE;
    }

    @Override
    public int resetUserPwd(Long userId, String password) {
        SysUser user = sysUserData.findById(userId);
        user.setPassword(password);
        return sysUserData.save(user) != null ? 1 : 0;
    }

    @Override
    public int deleteUserById(Long userId) {
        return 0;
    }

    @Override
    public void deleteUserByIds(Collection<Long> userIds) {
        sysUserData.deleteByIds(userIds);
    }
}
