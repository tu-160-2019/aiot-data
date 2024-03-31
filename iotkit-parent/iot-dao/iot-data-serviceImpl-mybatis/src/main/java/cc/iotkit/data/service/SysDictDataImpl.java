package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysDictData;
import cc.iotkit.data.system.ISysDictData;
import cc.iotkit.model.system.SysDictData;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysDictDataImpl implements ISysDictData, IJPACommData<SysDictData, Long, TbSysDictData> {

    @Resource
    @Qualifier("DBSysDictDataServiceImpl")
    private SysDictDataService sysDictDataService;

    @Override
    public SysDictDataService getBaseRepository() {
        return sysDictDataService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysDictData.class;
    }

    @Override
    public Class getTClass() {
        return SysDictData.class;
    }


    @Override
    public List<SysDictData> findByConditions(SysDictData query) {
        LambdaQueryWrapper<TbSysDictData> lambdaQueryWrapper = buildQueryCondition(query);
        lambdaQueryWrapper.orderByAsc(TbSysDictData::getDictSort);
        List<TbSysDictData> sysDictDataList = sysDictDataService.findByCondition(lambdaQueryWrapper);
        return MapstructUtils.convert(sysDictDataList, SysDictData.class);
    }

    @Override
    public Paging<SysDictData> findAll(PageRequest<SysDictData> pageRequest) {
        Page<TbSysDictData> paged = sysDictDataService.findPageByCondition(buildQueryCondition(pageRequest.getData())
                , pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysDictData.class));
    }

    @Override
    public SysDictData findByDictTypeAndDictValue(String dictType, String dictValue) {
        return null;
    }

    @Override
    public List<SysDictData> findByDicType(String dictType) {
        LambdaQueryWrapper<TbSysDictData> lambdaQueryWrapper = new LambdaQueryWrapper<TbSysDictData>()
                .eq(TbSysDictData::getStatus, UserConstants.DICT_NORMAL)
                .eq(TbSysDictData::getDictType, dictType);
        lambdaQueryWrapper.orderByAsc(TbSysDictData::getDictSort);
        List<TbSysDictData> sysDictDataList = sysDictDataService.findByCondition(lambdaQueryWrapper);
        return MapstructUtils.convert(sysDictDataList, SysDictData.class);
    }

    @Override
    public long countByDicType(String dictType) {
        return 0;
    }

    private LambdaQueryWrapper<TbSysDictData> buildQueryCondition(SysDictData data) {
        return new LambdaQueryWrapper<TbSysDictData>()
                .eq(null != data.getDictSort(), TbSysDictData::getDictSort, data.getDictSort())
                .eq(StringUtils.isNotEmpty(data.getStatus()), TbSysDictData::getStatus, data.getStatus())
                .eq(StringUtils.isNotEmpty(data.getDictType()), TbSysDictData::getDictType, data.getDictType())
                .like(StringUtils.isNotEmpty(data.getDictLabel()), TbSysDictData::getDictLabel, data.getDictLabel());
    }
}
