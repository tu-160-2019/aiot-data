package cc.iotkit.data.service.impl;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.RuleInfoMapper;
import cc.iotkit.data.model.TbRuleInfo;
import cc.iotkit.data.service.RuleInfoService;
import cc.iotkit.model.rule.RuleInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class RuleInfoServiceImpl extends ServiceImpl<RuleInfoMapper, TbRuleInfo> implements RuleInfoService {
    @Override
    public List<RuleInfo> findByUidAndType(String uid, String type) {
        List<TbRuleInfo> tbRuleInfoList = this.baseMapper.selectList(new LambdaQueryWrapper<TbRuleInfo>().eq(StringUtils.isNotBlank(uid), TbRuleInfo::getUid, uid).eq(StringUtils.isNotBlank(type), TbRuleInfo::getType, type));
        return MapstructUtils.convert(tbRuleInfoList, RuleInfo.class);
    }

    @Override
    public Paging<RuleInfo> findByUidAndType(String uid, String type, int page, int size) {
        Page<TbRuleInfo> rowPage = new Page<>(page, size);
        Page<TbRuleInfo> deviceGroupPage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbRuleInfo>().eq(TbRuleInfo::getUid, uid).eq(TbRuleInfo::getType, type));
        Paging<RuleInfo> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(deviceGroupPage.getRecords(), RuleInfo.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }

    @Override
    public Page<TbRuleInfo> findAll(int page, int size) {
        Page<TbRuleInfo> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<>());
    }

    @Override
    public Paging<RuleInfo> findByType(String type, int page, int size) {
        Page<TbRuleInfo> rowPage = new Page<>(page, size);
        Page<TbRuleInfo> deviceGroupPage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbRuleInfo>().eq(TbRuleInfo::getType, type));
        Paging<RuleInfo> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(deviceGroupPage.getRecords(), RuleInfo.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }

    @Override
    public List<RuleInfo> findByUid(String uid) {
        List<TbRuleInfo> tbRuleInfoList = this.baseMapper.selectList(new LambdaQueryWrapper<TbRuleInfo>().eq(StringUtils.isNotBlank(uid), TbRuleInfo::getUid, uid));
        return MapstructUtils.convert(tbRuleInfoList, RuleInfo.class);
    }

    @Override
    public Paging<RuleInfo> findByUid(String uid, int page, int size) {
        Page<TbRuleInfo> rowPage = new Page<>(page, size);
        Page<TbRuleInfo> deviceGroupPage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbRuleInfo>().eq(TbRuleInfo::getUid, uid));
        Paging<RuleInfo> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(deviceGroupPage.getRecords(), RuleInfo.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }

    @Override
    public long countByUid(String uid) {
        return this.baseMapper.selectCount(new LambdaQueryWrapper<TbRuleInfo>().eq(StringUtils.isNotBlank(uid), TbRuleInfo::getUid, uid));
    }
}
