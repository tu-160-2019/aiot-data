package cc.iotkit.manager.dto.vo.thingmodel;

import cc.iotkit.model.product.ThingModel;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel(value = "ThingModelVo")
@Data
@AutoMapper(target = ThingModel.class)
public class ThingModelVo implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "模型内容")
    private ThingModel.Model model;

    @ApiModelProperty(value = "产品key")
    private String productKey;

}
