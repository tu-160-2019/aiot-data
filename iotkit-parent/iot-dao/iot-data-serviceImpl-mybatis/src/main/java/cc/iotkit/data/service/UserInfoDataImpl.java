/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.UserInfoRepository;
import cc.iotkit.data.manager.IUserInfoData;
import cc.iotkit.data.model.TbUserInfo;
import cc.iotkit.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class UserInfoDataImpl implements IUserInfoData, IJPACommData<UserInfo, Long> {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo findByUid(String uid) {
        return MapstructUtils.convert(userInfoRepository.findByUid(uid), UserInfo.class);
    }

    @Override
    public List<UserInfo> findByType(int type) {
        return MapstructUtils.convert(userInfoRepository.findByType(type), UserInfo.class);
    }

    @Override
    public JpaRepository getBaseRepository() {
        return userInfoRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbUserInfo.class;
    }

    @Override
    public Class getTClass() {
        return UserInfo.class;
    }


}
