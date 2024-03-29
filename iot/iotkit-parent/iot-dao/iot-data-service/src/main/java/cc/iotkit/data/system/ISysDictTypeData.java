package cc.iotkit.data.system;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysDictType;

import java.util.List;

/**
 * 字典类型数据接口
 *
 * @author sjg
 */
public interface ISysDictTypeData extends ICommonData<SysDictType, Long> {

    /**
     * 按条件查询
     */
    List<SysDictType> findByConditions(SysDictType cond);

    /**
     * 按条件分页查询
     */
    Paging<SysDictType> findByConditions(SysDictType cond, int page, int size);

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    SysDictType findByDicType(String dictType);

    /**
     * 修改字典类型
     *
     * @param dictType 类型
     * @param newType  新类型
     */
    void updateDicType(String dictType, String newType);

    boolean checkDictTypeUnique(SysDictType dictType);
}
