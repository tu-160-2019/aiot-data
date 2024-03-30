package cc.iotkit.manager.dto.bo.channel;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.notify.ChannelConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "ChannelConfigBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChannelConfig.class)
public class ChannelConfigBo extends BaseDto  {

	private static final long serialVersionUID = -1L;

	@ApiModelProperty(value="id")
	private Long id;

	@ApiModelProperty(value="通道id")
	private Long channelId;

	@ApiModelProperty(value="通道配置名称")
	private String title;

	@ApiModelProperty(value="通道配置参数")
	private String param;

	@ApiModelProperty(value="创建时间")
	private Long createAt;

    }
