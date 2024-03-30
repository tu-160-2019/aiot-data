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

import cc.iotkit.common.utils.JsonUtils;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TdRestApi {

    @Value("${spring.td-datasource.url}")
    private String url;

    @Value("${spring.td-datasource.username}")
    private String username;

    @Value("${spring.td-datasource.password}")
    private String password;

    private String getRestApiUrl() {
        //jdbc:TAOS-RS://127.0.0.1:6041/iotkit?xxxx
        String restUrl = url.replace("jdbc:TAOS-RS://", "")
                .replaceAll("\\?.*", "");
        // /rest/sql/iotkit
        int idx = restUrl.lastIndexOf("/");
        //127.0.0.1:6041/rest/sql/iotkit
        return String.format("%s/rest/sql/%s", restUrl.substring(0, idx), restUrl.substring(idx + 1));
    }


    /**
     * 新建td api请求对象
     */
    public HttpRequest newApiRequest(String sql) {
        return HttpRequest
                .post(getRestApiUrl())
                .body(sql)
                .basicAuth(username, password);
    }

    /**
     * 执行sql
     */
    public TdResponse execSql(String sql) {
        log.info("exec td sql:{}", sql);
        HttpRequest request = newApiRequest(sql);
        HttpResponse response = request.execute();
        return JsonUtils.parseObject(response.body(), TdResponse.class);
    }


}
