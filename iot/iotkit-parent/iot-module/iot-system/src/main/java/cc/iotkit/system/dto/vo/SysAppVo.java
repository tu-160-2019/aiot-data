package cc.iotkit.system.dto.vo;

import cc.iotkit.model.system.SysApp;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 应用信息视图对象 SYS_APP
 *
 * @author tfd
 * @date 2023-08-10
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysApp.class)
public class SysAppVo implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * appId
     */
    @ApiModelProperty(value = "appId")
    private String appId;

    /**
     * appSecret
     */
    @ApiModelProperty(value = "appSecret")
    private String appSecret;

    /**
     * 应用类型
     */
    @ApiModelProperty(value = "应用类型")
    private String appType;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


}
