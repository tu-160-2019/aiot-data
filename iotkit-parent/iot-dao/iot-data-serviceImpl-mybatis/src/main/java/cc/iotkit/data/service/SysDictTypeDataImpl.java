package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.model.TbSysDictType;
import cc.iotkit.data.system.ISysDictTypeData;
import cc.iotkit.model.system.SysDictType;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysDictTypeDataImpl implements ISysDictTypeData, IJPACommData<SysDictType, Long> {
//public class SysDictTypeDataImpl implements ISysDictTypeData, IJPACommData<SysDictType, Long, TbSysDictType> {

    @Resource
    @Qualifier("DBSysDictTypeServiceImpl")
    private SysDictTypeService sysDictTypeService;


    @Override
    public Paging<SysDictType> findAll(PageRequest<SysDictType> pageRequest) {
        Page<TbSysDictType> paged = sysDictTypeService.findPageByConditions(
                buildQueryCondition(pageRequest.getData()), pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysDictType.class));
    }

    @Override
    public SysDictTypeService getBaseRepository() {
        return sysDictTypeService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysDictType.class;
    }

    @Override
    public Class getTClass() {
        return SysDictType.class;
    }


    @Override
    public List<SysDictType> findByConditions(SysDictType query) {
        List<TbSysDictType> rets = sysDictTypeService.findByConditions(buildQueryCondition(query));
        return MapstructUtils.convert(rets, SysDictType.class);
    }

    @Override
    public Paging<SysDictType> findByConditions(SysDictType query, int page, int size) {
        return null;
    }

    @Override
    public SysDictType findByDicType(String dictType) {
        return null;
    }

    @Override
    public void updateDicType(String dictType, String newType) {

    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        LambdaQueryWrapper<TbSysDictType> queryWrapper = new LambdaQueryWrapper<TbSysDictType>()
                .eq(TbSysDictType::getDictType, dictType)
                .ne(Objects.nonNull(dictType.getId()), TbSysDictType::getId, dictType.getId());

        final TbSysDictType ret = sysDictTypeService.findOneByConditions(queryWrapper);
        return Objects.nonNull(ret);
    }

    private LambdaQueryWrapper<TbSysDictType> buildQueryCondition(SysDictType data) {
        return new LambdaQueryWrapper<TbSysDictType>()
                .like(StringUtils.isNotBlank(data.getDictName()), TbSysDictType::getDictName, data.getDictName())
                .like(StringUtils.isNotBlank(data.getDictType()), TbSysDictType::getDictType, data.getDictType())
                .eq(StringUtils.isNotBlank(data.getStatus()), TbSysDictType::getStatus, data.getStatus());
    }


}
