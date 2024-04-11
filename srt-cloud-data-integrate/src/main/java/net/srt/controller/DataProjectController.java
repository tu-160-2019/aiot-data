package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.convert.DataProjectConvert;
import net.srt.entity.DataProjectEntity;
import net.srt.framework.common.cache.RedisCache;
import net.srt.framework.common.cache.RedisKeys;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.Result;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.framework.security.utils.TokenUtils;
import net.srt.query.DataProjectQuery;
import net.srt.service.DataProjectService;
import net.srt.vo.DataDatabaseVO;
import net.srt.vo.DataProjectVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 数据项目
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-09-27
 */
@RestController
@RequestMapping("project")
@Tag(name = "数据项目")
@AllArgsConstructor
public class DataProjectController {
	private final DataProjectService dataProjectService;
	private final TokenStoreCache storeCache;

	@GetMapping("page")
	@Operation(summary = "分页")
	@PreAuthorize("hasAuthority('data-integrate:project:page')")
	public Result<PageResult<DataProjectVO>> page(@Valid DataProjectQuery query) {
		PageResult<DataProjectVO> page = dataProjectService.page(query);

		return Result.ok(page);
	}

	@GetMapping("{id}")
	@Operation(summary = "信息")
	@PreAuthorize("hasAuthority('data-integrate:project:info')")
	public Result<DataProjectVO> get(@PathVariable("id") Long id) {
		DataProjectEntity entity = dataProjectService.getById(id);

		return Result.ok(DataProjectConvert.INSTANCE.convert(entity));
	}

	@PostMapping
	@Operation(summary = "保存")
	@PreAuthorize("hasAuthority('data-integrate:project:save')")
	public Result<String> save(@RequestBody DataProjectVO vo) {
		dataProjectService.save(vo);

		return Result.ok();
	}

	@PutMapping
	@Operation(summary = "修改")
	@PreAuthorize("hasAuthority('data-integrate:project:update')")
	public Result<String> update(@RequestBody @Valid DataProjectVO vo) {
		dataProjectService.update(vo);

		return Result.ok();
	}

	@DeleteMapping
	@Operation(summary = "删除")
	@PreAuthorize("hasAuthority('data-integrate:project:delete')")
	public Result<String> delete(@RequestBody List<Long> idList) {
		dataProjectService.delete(idList);

		return Result.ok();
	}

	@PostMapping("adduser/{projectId}")
	@Operation(summary = "添加成员")
	@PreAuthorize("hasAuthority('data-integrate:project:adduser')")
	public Result<String> addUser(@PathVariable Long projectId, @RequestBody List<Long> userIds) {
		dataProjectService.addUser(projectId, userIds);
		return Result.ok();
	}

	@GetMapping("/current-user-projects")
	@Operation(summary = "获取当前用户拥有的项目")
	public Result<List<DataProjectVO>> listProjects() {
		return Result.ok(dataProjectService.listProjects());
	}

	@PutMapping("/change-project/{projectId}")
	@Operation(summary = "切换项目（租户）")
	public Result<String> changeProject(@PathVariable Long projectId, HttpServletRequest request) {
		String accessToken = TokenUtils.getAccessToken(request);
		//把当前用户的租户id存储到redis缓存中，24小时过期
		storeCache.saveProjectId(accessToken, projectId);
		return Result.ok();
	}

	@PostMapping("/test-online")
	@Operation(summary = "测试连接")
	public Result<String> testOnline(@RequestBody @Valid DataProjectVO vo) {
		dataProjectService.testOnline(vo);
		return Result.ok();
	}
}
