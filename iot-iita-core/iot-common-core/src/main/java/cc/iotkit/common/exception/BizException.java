/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.exception;

import cc.iotkit.common.enums.ErrCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BizException extends RuntimeException {

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public BizException(String message) {
        super(message);
        this.message = message;
        this.code = ErrCode.SYSTEM_EXCEPTION.getKey();
    }

    /**
     * 统一异常消息处理
     *
     * @param errCode 异常枚举值
     */
    public BizException(ErrCode errCode) {
        this.message = errCode.getValue();
        this.code = errCode.getKey();
    }

    public BizException(ErrCode errCode, Throwable cause) {
        super(cause);
        this.code = errCode.getKey();
        this.message = errCode.getValue();
    }

    public BizException(ErrCode errCode, String message) {
        this.message = message;
        this.code = errCode.getKey();
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

}
