package net.srt.api.module.data.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据服务-api配置
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-01-28
*/
@Data
@Schema(description = "数据服务-api配置")
public class DataServiceApiConfigDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "分组id")
	private Long groupId;

	@Schema(description = "api地址")
	private String path;

	private String type;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "描述")
	private String note;

	@Schema(description = "sql语句")
	private String sqlText;
	private String sqlSeparator;
	private Integer sqlMaxRow;

	private String sqlParam;

	@Schema(description = "application/json 类API对应的json参数示例")
	private String jsonParam;
	private String responseResult;

	@Schema(description = "参数类型")
	private String contentType;

	@Schema(description = "是否发布 0-否 1-是")
	private Integer status;

	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date releaseTime;
	private Long releaseUserId;

	@Schema(description = "1-数据库 2-中台库")
	private Integer sqlDbType;

	@Schema(description = "数据库id")
	private Long databaseId;

	@Schema(description = "是否私有 0-否 1-是")
	private Integer previlege;

	@Schema(description = "是否开启事务 0-否 1-是")
	private Integer openTrans;

	@Schema(description = "项目id")
	private Long projectId;
	private Long orgId;

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

	@Schema(description = "已调用次数")
	private Integer requestedTimes;
	private Integer requestedSuccessTimes;
	private Integer requestedFailedTimes;

	private Long authId;

	private String group;

}
