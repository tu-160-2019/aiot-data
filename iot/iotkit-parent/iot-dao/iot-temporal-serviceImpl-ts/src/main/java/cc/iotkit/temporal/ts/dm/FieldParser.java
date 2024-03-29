/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.dm;


import cc.iotkit.model.product.ThingModel;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldParser {

    /**
     * 物模型到td数据类型映射
     */
    private static final Map<String, DataType> TYPE_MAPPING = Map.of(
            "int32", SQLDataType.INTEGER,
            "float", SQLDataType.FLOAT,
            "bool", SQLDataType.INTEGER,
            "enum", SQLDataType.INTEGER,
            "text", SQLDataType.NVARCHAR,
            "date", SQLDataType.DATE,
            "position", SQLDataType.NVARCHAR
    );

    /**
     * td数据类型到物模型映射
     */

    private static final Map<String, DataType> DB2TYPE_MAPPING = Map.of(
            "int",SQLDataType.INTEGER,
            "float",   SQLDataType.FLOAT,
            "bool", SQLDataType.INTEGER,
            "char",SQLDataType.NVARCHAR,
            "date",  SQLDataType.DATE,
            "timestamptz", SQLDataType.TIMESTAMPWITHTIMEZONE
    );


    private static DataType getFieldType(final String type) {
        Set<String> keys = DB2TYPE_MAPPING.keySet();
        String lowerCase = type.toLowerCase();
        for(String key:keys){
            if(lowerCase.contains(key)){
                return DB2TYPE_MAPPING.get(key);
            }
        }
        return null;
    }

    /**
     * 将物模型字段转换为td字段
     */
    public static TsField parse(ThingModel.Property property) {
        String filedName = property.getIdentifier().toLowerCase();
        ThingModel.DataType dataType = property.getDataType();
        String type = dataType.getType();

        //将物模型字段类型映射为td字段类型
        DataType fType = TYPE_MAPPING.get(type);
        Object specs = dataType.getSpecs();
        int len = -1;
        if (specs instanceof Map) {
            Object objLen = ((Map<?, ?>) specs).get("length");
            if (objLen != null) {
                len = Integer.parseInt(objLen.toString());
            }
        }

        return new TsField(filedName, fType, len);
    }

    /**
     * 获取物模型中的字段列表
     */
    public static List<TsField> parse(ThingModel thingModel) {
        return thingModel.getModel().getProperties().stream().map(FieldParser::parse).collect(Collectors.toList());
    }

    /**
     * 将从库中查出来的字段信息转换为td字段对象
     */
    public static List<TsField> parse(List<DbField> rows) {
        return (List<TsField>) rows.stream().map((r) -> {

            return new TsField(
                    r.getName(),
                    getFieldType(r.getType()).length(r.getLength()),r.getLength());
        }).collect(Collectors.toList());
    }

    /**
     * 获取字段字义
     */
    public static Field getFieldDefine(TsField field) {
        int length = field.getLength();
        DataType type = field.getType();

        if(length>0){
            type.length(length);
        }
        return DSL.field(field.getName(),type);

    }

}
