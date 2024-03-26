package net.srt.system.service;

import net.srt.framework.mybatis.service.BaseService;
import net.srt.system.entity.SysOrgEntity;
import net.srt.system.vo.SysOrgVO;

import java.util.List;

/**
 * 机构管理
 *
 * @author 阿沐 babamu@126.com
 */
public interface SysOrgService extends BaseService<SysOrgEntity> {

	List<SysOrgVO> getList(Long parentId);

	void save(SysOrgVO vo);

	void update(SysOrgVO vo);

	void delete(Long id);

	/**
	 * 根据机构ID，获取子机构ID列表(包含本机构ID)
	 * @param id   机构ID
	 */
	List<Long> getSubOrgIdList(Long id);
}
