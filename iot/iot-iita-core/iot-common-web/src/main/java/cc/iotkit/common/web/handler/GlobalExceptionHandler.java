/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.web.handler;

import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.exception.ViewException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RequestResult handleException(Exception e, HttpServletResponse response) {
        log.error("handler exception", e);
        if (e instanceof NotLoginException) {
            response.setStatus(401);
            return new RequestResult(401, "未授权的请求");
        }

        if (e instanceof NotPermissionException || e instanceof NotRoleException) {
            response.setStatus(403);
            return new RequestResult(403, "没有权限");
        }
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            response.setStatus(200);
            return new RequestResult(bizException.getCode(), bizException.getMessage());
        }
        if (e instanceof ViewException) {
            response.setStatus(200);
            return new RequestResult(((ViewException) e).getCode(), e.getMessage());
        }

        if (e.getMessage().contains("Unauthorized")) {
            response.setStatus(403);
            return new RequestResult(403, "没有权限");
        }
        response.setStatus(500);
        return new RequestResult(500, e.getMessage());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class RequestResult {
        private int code;
        private String message;
    }

}


