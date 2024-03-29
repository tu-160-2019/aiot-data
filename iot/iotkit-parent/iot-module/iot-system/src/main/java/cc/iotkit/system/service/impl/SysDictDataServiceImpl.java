package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.CacheNames;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.system.ISysDictData;
import cc.iotkit.model.system.SysDictData;
import cc.iotkit.system.dto.bo.SysDictDataBo;
import cc.iotkit.system.dto.vo.SysDictDataVo;
import cc.iotkit.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    private final ISysDictData sysDictData;

    @Override
    public Paging<SysDictDataVo> selectPageDictDataList( PageRequest<SysDictDataBo> query) {
        return sysDictData.findAll(query.to(SysDictData.class) ).to(SysDictDataVo.class);
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictDataVo> selectDictDataList(SysDictDataBo dictData) {
        return MapstructUtils.convert(
                sysDictData.findByConditions(
                        MapstructUtils.convert(dictData, SysDictData.class)
                ), SysDictDataVo.class);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return sysDictData.findByDictTypeAndDictValue(dictType, dictValue).getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictDataVo selectDictDataById(Long dictCode) {
        return MapstructUtils.convert(sysDictData.findById(dictCode), SysDictDataVo.class);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = sysDictData.findById(dictCode);
            sysDictData.deleteById(dictCode);
//            CacheUtils.evict(CacheNames.SYS_DICT, data.getDictType());
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
    @Override
    public List<SysDictDataVo> insertDictData(SysDictDataBo bo) {
        SysDictData data = MapstructUtils.convert(bo, SysDictData.class);
        sysDictData.save(data);
        return MapstructUtils.convert(sysDictData.findByDicType(data.getDictType()), SysDictDataVo.class);
    }

    /**
     * 修改保存字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
    @Override
    public List<SysDictDataVo> updateDictData(SysDictDataBo bo) {
        SysDictData data = MapstructUtils.convert(bo, SysDictData.class);
        sysDictData.save(data);
        return MapstructUtils.convert(sysDictData.findByDicType(data.getDictType()), SysDictDataVo.class);
    }

}
