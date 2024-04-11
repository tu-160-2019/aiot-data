package net.srt.service;

import net.srt.entity.DataFileCategoryEntity;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.DataFileCategoryVO;

import java.util.List;

/**
 * 文件分组表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-12
 */
public interface DataFileCategoryService extends BaseService<DataFileCategoryEntity> {

    void save(DataFileCategoryVO vo);

    void update(DataFileCategoryVO vo);

    void delete(Long id);

	List<TreeNodeVo> listTree();
}
