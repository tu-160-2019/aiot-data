package cc.iotkit.openapi.dto.vo;

import cc.iotkit.model.product.ThingModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "OpenPropertyVo")
@Data
public class OpenPropertyVo {
    private String identifier;
    private ThingModel.DataType dataType;
    private String name;
    private String accessMode = "rw";

    // 描述
    private String description;

    // 单位
    private String unit;

    private String time;

    private String value;

    public OpenPropertyVo() {
    }

    public OpenPropertyVo(String identifier, ThingModel.DataType dataType, String name, String accessMode, String description, String unit) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.name = name;
        this.accessMode = accessMode;
        this.description = description;
        this.unit = unit;
    }
}
