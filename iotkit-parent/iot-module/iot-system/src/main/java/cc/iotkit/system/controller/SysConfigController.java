package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.model.product.Category;
import cc.iotkit.system.dto.bo.SysConfigBo;
import cc.iotkit.system.dto.vo.SysConfigVo;
import cc.iotkit.system.service.ISysConfigService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/config")
@Api(tags = "参数配置 信息操作处理")
public class SysConfigController extends BaseController {

  private final ISysConfigService configService;

  private final ICategoryData categoryData;


  @ApiOperation("获取参数配置列表")
  @SaCheckPermission("system:config:list")
  @PostMapping("/list")
  public Paging<SysConfigVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<SysConfigBo> query) {
    return configService.selectPageConfigList(query);
  }

  @ApiOperation("导出参数配置列表")
  @Log(title = "参数管理", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:config:export")
  @PostMapping("/export")
  public void export(@Validated(QueryGroup.class) SysConfigBo config,
      HttpServletResponse response) {
    List<SysConfigVo> list = configService.selectConfigList(config);
    ExcelUtil.exportExcel(list, "参数数据", SysConfigVo.class, response);
  }


  @ApiOperation("根据参数编号获取详细信息")
  @SaCheckPermission("system:config:query")
  @PostMapping(value = "/getDetail")
  public SysConfigVo getInfo(@RequestBody @Validated Request<Long> request) {
    return configService.selectConfigById(request.getData());
  }


  @ApiOperation("根据参数键名查询参数值")
  @PostMapping(value = "/getConfigKey")
  public String  getConfigKey(@RequestBody @Validated Request<String> request) {
    return configService.selectConfigByKey(request.getData());
  }


  @ApiOperation("新增参数配置")
  @SaCheckPermission("system:config:add")
  @Log(title = "参数管理", businessType = BusinessType.INSERT)
  @PostMapping(value = "/add")
  public void add(@RequestBody @Validated(EditGroup.class) Request<SysConfigBo> request) {
    if (!configService.checkConfigKeyUnique(request.getData())) {
      fail("新增参数'" + request.getData().getConfigName() + "'失败，参数键名已存在");
    }
     configService.insertConfig(request.getData());
  }

  @ApiOperation("修改参数配置")
  @SaCheckPermission("system:config:edit")
  @Log(title = "参数管理", businessType = BusinessType.UPDATE)
  @PostMapping(value = "/edit")
  public void edit(@RequestBody @Validated(EditGroup.class) Request<SysConfigBo> request) {
    if (!configService.checkConfigKeyUnique(request.getData())) {
      fail("修改参数'" + request.getData().getConfigName() + "'失败，参数键名已存在");
    }
    configService.updateConfig(request.getData());
  }

  @ApiOperation("根据参数键名修改参数配置")
  @SaCheckPermission("system:config:edit")
  @Log(title = "参数管理", businessType = BusinessType.UPDATE)
  @PostMapping("/updateByKey")
  public void updateByKey(@RequestBody @Validated(EditGroup.class) Request<SysConfigBo> request) {
    configService.updateConfig(request.getData());
  }

  @ApiOperation("删除参数配置")
  @SaCheckPermission("system:config:remove")
  @Log(title = "参数管理", businessType = BusinessType.DELETE)
  @PostMapping("/delete")
  public void remove(@RequestBody @Validated Request<List<Long>> request) {
    configService.deleteConfigByIds(request.getData());
  }

  @ApiOperation("刷新参数缓存")
  @SaCheckPermission("system:config:remove")
  @Log(title = "参数管理", businessType = BusinessType.CLEAN)
  @PostMapping("/refreshCache")
  public void refreshCache() {
    configService.resetConfigCache();
  }

  @SneakyThrows
  @ApiOperation("导出系统数据")
  @Log(title = "导出系统数据", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:config:exportSysData")
  @PostMapping("/exportSysData")
  public void exportSysData() {
    configService.backupSysData();
  }

}
