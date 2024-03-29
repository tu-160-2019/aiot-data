package cc.iotkit.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物模型数据类型
 *
 * @author sjg
 */
@Getter
@AllArgsConstructor
public enum ThingDataType {
    /**
     * int 整数
     */
    INT("int32", "整数"),
    /**
     * 小数
     */
    FLOAT("float", "小数"),
    /**
     * 布尔
     */
    BOOL("bool", "布尔"),
    /**
     * 枚举
     */
    ENUM("enum", "枚举"),
    /**
     * 文本字符
     */
    TEXT("text", "文本字符"),
    /**
     * 日期
     */
    DATE("date", "时间戳"),
    ;

    private final String code;
    private final String info;

}
