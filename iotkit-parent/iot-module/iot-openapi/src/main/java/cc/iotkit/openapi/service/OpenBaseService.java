package cc.iotkit.openapi.service;

import cc.iotkit.openapi.dto.bo.TokenVerifyBo;

/**
 * @Author: dsy
 * @Date: 2023/7/24 11:30
 * @Version: V1.0
 * @Description: openapi基础服务接口
 */
public interface OpenBaseService {

    String getToken(TokenVerifyBo bo);
}
