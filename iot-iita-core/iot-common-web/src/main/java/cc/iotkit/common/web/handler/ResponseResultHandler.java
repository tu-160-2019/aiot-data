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

import cc.iotkit.common.api.Response;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.IdUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@ControllerAdvice(basePackages = {"cc.iotkit"})
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getParameterType() == ResponseEntity.class) {
            return false;
        }
        return !converterType.equals(StringHttpMessageConverter.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof GlobalExceptionHandler.RequestResult) {
            GlobalExceptionHandler.RequestResult requestResult = (GlobalExceptionHandler.RequestResult) body;
            return new Response(requestResult.getCode(), requestResult.getMessage(),
                    "", IdUtil.simpleUUID());
        } else if (body instanceof SaResult) {
            SaResult result = (SaResult) body;
            return new Response(result.getCode(), result.getMsg(), result.getData(), IdUtil.simpleUUID());
        } else if (body instanceof Map) {
            Map map = (Map) body;
            //spring mvc内部异常
            if (map.containsKey("timestamp") && map.containsKey("status") && map.containsKey("error")) {
                return new Response((Integer) map.get("status"), (String) map.get("error"),
                        "", IdUtil.simpleUUID());
            }
        } else if (body instanceof Response) {
            return body;
        }

        return new Response(200, "", body, IdUtil.simpleUUID());
    }

}
