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

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.data.cache.OauthClientCacheEvict;
import cc.iotkit.data.manager.IOauthClientData;
import cc.iotkit.model.OauthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Qualifier("oauthClientDataCache")
public class OauthClientDataCache implements IOauthClientData {

    @Autowired
    private IOauthClientData oauthClientData;
    @Autowired
    private OauthClientCacheEvict oauthClientCacheEvict;

    @Override
    @Cacheable(value = Constants.CACHE_OAUTH_CLIENT, key = "#root.method.name+#clientId", unless = "#result == null")
    public OauthClient findByClientId(String clientId) {
        return oauthClientData.findByClientId(clientId);
    }

    @Override
    public OauthClient findById(String s) {
        return oauthClientData.findById(s);
    }

    @Override
    public List<OauthClient> findByIds(Collection<String> id) {
        return Collections.emptyList();
    }

    @Override
    public OauthClient save(OauthClient data) {
        OauthClient oauthClient = oauthClientData.save(data);
        //清除缓存
        oauthClientCacheEvict.findByClientId(data.getClientId());
        return oauthClient;
    }

    @Override
    public void batchSave(List<OauthClient> data) {

    }

    @Override
    public void deleteById(String s) {
        oauthClientData.deleteById(s);
    }

    @Override
    public void deleteByIds(Collection<String> strings) {

    }

    @Override
    public long count() {
        return oauthClientData.count();
    }

    @Override
    public List<OauthClient> findAll() {
        return oauthClientData.findAll();
    }

    @Override
    public Paging<OauthClient> findAll(PageRequest<OauthClient> pageRequest) {
        return oauthClientData.findAll(pageRequest);
    }

    @Override
    public List<OauthClient> findAllByCondition(OauthClient data) {
        return Collections.emptyList();
    }

    @Override
    public OauthClient findOneByCondition(OauthClient data) {
        return null;
    }

}
