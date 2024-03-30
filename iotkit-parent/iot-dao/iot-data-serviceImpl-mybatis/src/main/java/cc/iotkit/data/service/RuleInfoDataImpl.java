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
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IRuleInfoData;
import cc.iotkit.data.dao.RuleInfoRepository;
import cc.iotkit.data.model.TbRuleInfo;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.model.TbTaskInfo;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.model.rule.FilterConfig;
import cc.iotkit.model.rule.RuleAction;
import cc.iotkit.model.rule.RuleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
public class RuleInfoDataImpl implements IRuleInfoData, IJPACommData<RuleInfo, String> {

    @Autowired
    private RuleInfoRepository ruleInfoRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return ruleInfoRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbRuleInfo.class;
    }

    @Override
    public Class getTClass() {
        return RuleInfo.class;
    }

    @Override
    public List<RuleInfo> findByUidAndType(String uid, String type) {
        return fromTb(ruleInfoRepository.findByUidAndType(uid, type));
    }

    @Override
    public Paging<RuleInfo> findByUidAndType(String uid, String type, int page, int size) {
        Page<TbRuleInfo> paged = ruleInfoRepository.findByUidAndType(uid, type,
                Pageable.ofSize(size).withPage(page - 1));

        return new Paging<>(paged.getTotalElements(),
                fromTb(paged.getContent()));
    }

    @Override
    public Paging<RuleInfo> findByType(String type, int page, int size) {
        Page<TbRuleInfo> paged = ruleInfoRepository.findByType(type,
                Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(paged.getTotalElements(),
                fromTb(paged.getContent()));
    }

    @Override
    public List<RuleInfo> findByUid(String uid) {
        return fromTb(ruleInfoRepository.findByUid(uid));
    }

    @Override
    public Paging<RuleInfo> findByUid(String uid, int page, int size) {
        Page<TbRuleInfo> paged = ruleInfoRepository.findByUid(uid,
                Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(paged.getTotalElements(), fromTb(paged.getContent()));
    }

    @Override
    public long countByUid(String uid) {
        return ruleInfoRepository.countByUid(uid);
    }


    @Override
    public RuleInfo findById(String s) {
        return from(ruleInfoRepository.findById(s).orElse(null));
    }

    @Override
    public List<RuleInfo> findByIds(Collection<String> id) {
        return null;
    }

    @Override
    public Paging<RuleInfo> findAll(PageRequest<RuleInfo> pageRequest) {
        Page<TbRuleInfo> ret = ruleInfoRepository.findAll(PageBuilder.toPageable(pageRequest));
        return new Paging<>(ret.getTotalElements(), fromTb(ret.getContent()));
    }

    @Override
    public RuleInfo save(RuleInfo data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
            data.setCreateAt(System.currentTimeMillis());
        }
        ruleInfoRepository.save(from(data));
        return data;
    }

    private static RuleInfo from(TbRuleInfo tb) {
        RuleInfo convert = MapstructUtils.convert(tb, RuleInfo.class);
        assert convert != null;
        convert.setActions(JsonUtils.parseArray(tb.getActions(), RuleAction.class));
        convert.setFilters(JsonUtils.parseArray(tb.getFilters(), FilterConfig.class));
        convert.setListeners(JsonUtils.parseArray(tb.getListeners(), FilterConfig.class));
        return convert;
    }

    private static TbRuleInfo from(RuleInfo rule) {
        TbRuleInfo convert = MapstructUtils.convert(rule, TbRuleInfo.class);
        assert convert != null;
        convert.setActions(JsonUtils.toJsonString(rule.getActions()));
        convert.setFilters(JsonUtils.toJsonString(rule.getFilters()));
        convert.setListeners(JsonUtils.toJsonString(rule.getListeners()));
        return convert;
    }

    private static List<RuleInfo> fromTb(List<TbRuleInfo> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(RuleInfoDataImpl::from).collect(Collectors.toList());
    }

    private static List<TbRuleInfo> fromBo(List<RuleInfo> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(RuleInfoDataImpl::from).collect(Collectors.toList());
    }
}
