package net.srt.api.module.data.integrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

import java.io.Serializable;
import java.util.Date;

/**
* 数据集成-数据库管理
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-09
*/
@Data
@Schema(description = "数据集成-数据库管理")
public class DataDatabaseDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "数据库类型")
	private Integer databaseType;

	@Schema(description = "主机ip")
	private String databaseIp;

	@Schema(description = "端口")
	private String databasePort;

	@Schema(description = "库名(服务名)")
	private String databaseName;

	private String databaseSchema;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "用户名")
	private String userName;

	@Schema(description = "密码")
	private String password;

	@Schema(description = "是否支持实时接入")
	private Integer isRtApprove;

	@Schema(description = "不支持实时接入原因")
	private String noRtReason;

	@Schema(description = "jdbcUrl")
	private String jdbcUrl;

	@Schema(description = "所属项目")
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

}
