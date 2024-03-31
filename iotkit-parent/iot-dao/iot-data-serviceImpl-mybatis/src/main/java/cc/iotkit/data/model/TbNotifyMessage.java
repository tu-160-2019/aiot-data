package cc.iotkit.data.model;

import cc.iotkit.model.notify.NotifyMessage;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel(value = "通知消息")
@TableName("notify_message")
@AutoMapper(target= NotifyMessage.class)
public class TbNotifyMessage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "通知消息id")
    private Long id;

    private String content;

    private String messageType;

    private Boolean status;

    private Long createAt;

    private Long updateAt;
}
