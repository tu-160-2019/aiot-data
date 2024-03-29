package cc.iotkit.openapi.controller;

import cc.iotkit.common.api.Request;
import cc.iotkit.model.InvokeResult;
import cc.iotkit.openapi.dto.bo.TokenVerifyBo;
import cc.iotkit.openapi.service.OpenBaseService;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"openapi-基础"})
@Slf4j
@RestController
@RequestMapping("/openapi")
public class OpenAuthController {

    @Autowired
    private OpenBaseService openBaseService;

    @SaIgnore
    @ApiOperation(value = "token获取", notes = "token获取", httpMethod = "POST")
    @PostMapping("/v1/getToken")
    public InvokeResult getToken(@RequestBody @Validated Request<TokenVerifyBo> request) {
        return new InvokeResult(openBaseService.getToken(request.getData()));
    }


}
