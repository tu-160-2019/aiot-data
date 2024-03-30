package cc.iotkit.plugin.core.thing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品信息
 *
 * @author sjg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThingProduct {

    private String productKey;

    private String productSecret;

    private String name;

    private String category;

    private Integer nodeType;

}
