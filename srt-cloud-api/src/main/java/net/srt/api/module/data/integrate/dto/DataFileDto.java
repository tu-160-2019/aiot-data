package net.srt.api.module.data.integrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 文件表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-16
*/
@Data
@Schema(description = "文件表")
public class DataFileDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "所属分组id")
	private Integer fileCategoryId;

	@Schema(description = "文件类型")
	private String type;

	@Schema(description = "文件url地址")
	private String fileUrl;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "大小")
	private Long size;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "项目id")
	private Long projectId;
	private Long orgId;

	@Schema(description = "删除标识  0：正常   1：已删除")
	private Integer deleted;

	@Schema(description = "创建者")
	private Long creator;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;

	@Schema(description = "更新者")
	private Long updater;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date updateTime;

	private String group;


}
