package cc.iotkit.data.model;

import cc.iotkit.model.product.ProductModel;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@ApiModel(value = "产品型号")
@TableName("product_model")
@AutoMapper(target = ProductModel.class)
public class TbProductModel {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "型号id")
    private String id;

    /**
     * 型号在所有产品中唯一
     */
    @ApiModelProperty(value = "型号")
    private String model;

    @ApiModelProperty(value = "名称")

    private String name;

    @ApiModelProperty(value = "产品Key")

    private String productKey;

    @ApiModelProperty(value = "脚本类型")
    private String type;

    @ApiModelProperty(value = "脚本内容")
    private String script;

    /**
     * 脚本状态，只有发布状态才生效
     */
    @ApiModelProperty(value = "脚本状态")

    private String state;
    @ApiModelProperty(value = "修改时间")
    private Long modifyAt;
}
