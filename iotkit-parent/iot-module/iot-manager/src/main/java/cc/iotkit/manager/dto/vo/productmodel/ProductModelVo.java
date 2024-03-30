package cc.iotkit.manager.dto.vo.productmodel;

import cc.iotkit.model.product.ProductModel;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel(value = "ProductModelVo")
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProductModel.class)

public class ProductModelVo implements Serializable  {

	private static final long serialVersionUID = -1L;

	@ApiModelProperty(value="型号id")
    @ExcelProperty(value = "型号id")
		private String id;

	@ApiModelProperty(value="型号")
    @ExcelProperty(value = "型号")
		private String model;

	@ApiModelProperty(value="修改时间")
    @ExcelProperty(value = "修改时间")
		private Long modifyAt;

	@ApiModelProperty(value="名称")
    @ExcelProperty(value = "名称")
		private String name;

	@ApiModelProperty(value="产品Key")
    @ExcelProperty(value = "产品Key")
		private String productKey;

	@ApiModelProperty(value="脚本内容")
    @ExcelProperty(value = "脚本内容")
		private String script;

	@ApiModelProperty(value="脚本状态")
    @ExcelProperty(value = "脚本状态")
		private String state;

	@ApiModelProperty(value="脚本类型")
    @ExcelProperty(value = "脚本类型")
		private String type;



}
