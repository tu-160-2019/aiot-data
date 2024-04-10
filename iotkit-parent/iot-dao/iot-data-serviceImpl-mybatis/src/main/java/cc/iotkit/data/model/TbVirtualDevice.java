/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.model;

import cc.iotkit.model.device.VirtualDevice;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("virtual_device")
@AutoMapper(target = VirtualDevice.class)
public class TbVirtualDevice {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属用户
     */
    @ApiModelProperty(value = "所属用户")
    private String uid;

    /**
     * 虚拟设备名称
     */
    @ApiModelProperty(value = "虚拟设备名称")
    private String name;

    /**
     * 产品key
     */
    @ApiModelProperty(value = "产品key")
    private String productKey;

    /**
     * 虚拟类型
     */
    @ApiModelProperty(value = "虚拟类型")
    private String type;

    /**
     * 设备行为脚本
     */
    @ApiModelProperty(value = "设备行为脚本")
    private String script;

    /**
     * 触发方式执行方式
     */
    @ApiModelProperty(value = "触发方式执行方式")
    @TableField("'trigger'")
    private String trigger;

    /**
     * 触发表达式
     */
    @ApiModelProperty(value = "触发表达式")
    private String triggerExpression;

    /**
     * 运行状态
     */
    @ApiModelProperty(value = "运行状态")
    private String state;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
