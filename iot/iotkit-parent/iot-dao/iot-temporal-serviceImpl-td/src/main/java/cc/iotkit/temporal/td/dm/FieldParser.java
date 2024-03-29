/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.dm;

import cc.iotkit.model.product.ThingModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldParser {

    /**
     * 物模型到td数据类型映射
     */
    private static final Map<String, String> TYPE_MAPPING = Map.of(
            "int32", "INT",
            "float", "FLOAT",
            "bool", "TINYINT",
            "enum", "TINYINT",
            "text", "NCHAR",
            "date", "NCHAR",
            "position", "NCHAR"
    );

    /**
     * 将物模型字段转换为td字段
     */
    public static TdField parse(ThingModel.Property property) {
        String filedName = property.getIdentifier().toLowerCase();
        ThingModel.DataType dataType = property.getDataType();
        String type = dataType.getType();

        //将物模型字段类型映射为td字段类型
        String fType = TYPE_MAPPING.get(type);
        Object specs = dataType.getSpecs();
        int len = -1;
        if (specs instanceof Map) {
            Object objLen = ((Map<?, ?>) specs).get("length");
            if (objLen != null) {
                len = Integer.parseInt(objLen.toString());
            }
        }

        return new TdField(filedName, fType, len);
    }

    /**
     * 获取物模型中的字段列表
     */
    public static List<TdField> parse(ThingModel thingModel) {
        return thingModel.getModel().getProperties().stream().map(FieldParser::parse).collect(Collectors.toList());
    }

    /**
     * 将从库中查出来的字段信息转换为td字段对象
     */
    public static List<TdField> parse(List rows) {
        return (List<TdField>) rows.stream().map((r) -> {
            List row = (List) r;
            String type = row.get(1).toString().toUpperCase();
            return new TdField(
                    row.get(0).toString(),
                    type,
                    type.equals("NCHAR") ? Integer.parseInt(row.get(2).toString()) : -1);
        }).collect(Collectors.toList());
    }

    /**
     * 获取字段字义
     */
    public static String getFieldDefine(TdField field) {
        return field.getName() + " " + (field.getLength() > 0 ?
                String.format("%s(%d)", field.getType(), field.getLength())
                : field.getType());
    }

}
