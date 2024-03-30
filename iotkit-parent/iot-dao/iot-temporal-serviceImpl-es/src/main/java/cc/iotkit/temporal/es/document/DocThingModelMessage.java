/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.document;

import cc.iotkit.common.thing.ThingModelMessage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "thing_model_message")
@AutoMapper(target = ThingModelMessage.class)
public class DocThingModelMessage {

    @Id
    private String id;

    private String mid;

    private String deviceId;

    private String productKey;

    private String deviceName;

    private String type;

    private String identifier;

    private int code;

    private Object data;

    @Field(type = FieldType.Date)
    private Long occurred;

    @Field(type = FieldType.Date)
    private Long time;

}
