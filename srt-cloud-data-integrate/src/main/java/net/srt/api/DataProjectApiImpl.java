package net.srt.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.integrate.DataProjectApi;
import net.srt.entity.DataProjectEntity;
import net.srt.entity.DataProjectUserRelEntity;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataProjectService;
import net.srt.service.DataProjectUserRelService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName DataAccessApiImpl
 * @Author zrx
 * @Date 2022/10/26 11:50
 */
@RestController
@RequiredArgsConstructor
public class DataProjectApiImpl implements DataProjectApi {

	private final DataProjectService dataProjectService;
	private final DataProjectUserRelService dataProjectUserRelService;


	@Override
	public Result<List<DataProjectCacheBean>> getProjectList() {
		List<DataProjectEntity> list = dataProjectService.list();
		return Result.ok(BeanUtil.copyListProperties(list, DataProjectCacheBean::new));
	}

	@Override
	public Result<DataProjectCacheBean> getById(Long id) {
		return Result.ok(BeanUtil.copyProperties(dataProjectService.getById(id), DataProjectCacheBean::new));
	}

	@Override
	public Result<String> addProjectRel(Long id, Long userId) {
		DataProjectUserRelEntity dataProjectUserRelEntity = new DataProjectUserRelEntity();
		dataProjectUserRelEntity.setDataProjectId(id);
		dataProjectUserRelEntity.setUserId(userId);
		DataProjectUserRelEntity relEntity = dataProjectUserRelService.getByProjectIdAndUserId(id, userId);
		if (relEntity == null) {
			dataProjectUserRelService.save(dataProjectUserRelEntity);
		}
		return Result.ok();
	}
}
