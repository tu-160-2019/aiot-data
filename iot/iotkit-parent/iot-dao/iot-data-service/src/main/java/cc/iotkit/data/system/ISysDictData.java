package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysDictData;

import java.util.List;

/**
 * 字典数据接口
 *
 * @author sjg
 */
public interface ISysDictData extends ICommonData<SysDictData, Long> {

    /**
     * 按条件查询
     */
    List<SysDictData> findByConditions(SysDictData query);



    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    SysDictData findByDictTypeAndDictValue(String dictType, String dictValue);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictData> findByDicType(String dictType);

    /**
     * 根据字典类型查询字典数据数量
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    long countByDicType(String dictType);

}
