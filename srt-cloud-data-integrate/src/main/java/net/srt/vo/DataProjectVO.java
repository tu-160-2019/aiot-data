package net.srt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据项目
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-09-27
*/
@Data
@Schema(description = "数据项目")
public class DataProjectVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "id")
	private Long id;

	@Schema(description = "项目名称")
	private String name;

	@Schema(description = "英文名称")
	private String engName;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "负责人")
	private String dutyPerson;

	@Schema(description = "版本号")
	private Integer version;

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
	private Integer dbType;
	private String dbIp;
	private String dbPort;
	private String dbName;
	private String dbSchema;
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;


}
