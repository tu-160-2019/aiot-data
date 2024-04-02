package cc.iotkit.screen.api;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.model.screen.ScreenApi;
import cc.iotkit.script.IScriptEngine;
import cc.iotkit.script.ScriptEngineFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:54
 */
@Slf4j
@Data
public class ScreenApiHandle {
    private final Long screenId;

    public List<ScreenApi> screenApis;

    public boolean debugMode=false;

    private final IScriptEngine scriptEngine = ScriptEngineFactory.getScriptEngine("js");

    public ScreenApiHandle(Long screenId, List<ScreenApi> screenApis) {
        this.screenId = screenId;
        this.screenApis = screenApis;
    }

    public void putScriptEnv(String key, Object value) {
        this.scriptEngine.putScriptEnv(key, value);
    }

    /**
     * 每个请求的处理方法要执行对应的请求脚本然后将结果返回给http服务器
     *
     */
    public String httpReq(HttpServerRequest req, HttpServerResponse res) {
        String response="";
        try {
            Map<String, Object> httpHeader = getData(req.headers());
            log.info("request header:{}", JsonUtils.toJsonString(httpHeader));
            String path=req.path();
            String params="";
            log.info("接收到请求:"+path);
            if(HttpMethod.GET.name().equals(req.method().name())){
                String[] getParams=req.absoluteURI().split("\\?");
                if(getParams.length>1){
                    params=getParams[1];
                }
            }else if(HttpMethod.POST.name().equals(req.method().name())){
                MultiMap postParams=req.params();
                JsonObject jsonObject = new JsonObject();
                if(!postParams.isEmpty()){
                    for (String paramName : postParams.names()) {
                        jsonObject.put(paramName, postParams.get(paramName));
                    }
                    params=jsonObject.encode();
                }
            }
            log.info("参数:"+params);
            if(debugMode){
                if(screenApis.stream().anyMatch(m -> m.getApiPath().equals(path))){
                    String finalParams = params;
                    screenApis.stream().map(s -> {
                        s.setApiPath(path);
                        s.setApiParams(finalParams);
                        s.setHttpMethod(req.method().name());
                        return s;
                    }).collect(Collectors.toList());
                }else{
                    ScreenApi sai=new ScreenApi();
                    sai.setApiPath(path);
                    sai.setApiParams(params);
                    sai.setHttpMethod(req.method().name());
                    screenApis.add(sai);
                }
                response="PREVIEW API";
            }else{
                for (ScreenApi screenApi:screenApis) {
                    if(screenApi.getApiPath().equals(path)){
                        String script=screenApi.getScript();
                        if(StringUtils.isBlank(script)){
                            response="转换脚本为空";
                            return response;
                        }
                        scriptEngine.setScript(script);
                        try {
                            HttpContent content =
                                    scriptEngine.invokeMethod(
                                            new TypeReference<>() {
                                            },
                                            "messageConver",
                                            httpHeader,
                                            params);
                            response = content.getContent();
                            response = response == null ? "" : response;
                        } catch (Throwable e) {
                            log.error("invokeMethod messageConver error", e);
                            response = e.getMessage();
                            return response;
                        }
                        log.info("response,content:{}", response);
                    }else if("/favicon.ico".equals(path)){
                        response="NOT FOUND";
                    }else{
                        response="NOT FOUND";
                    }
                }
            }
        } catch (Throwable e) {
            log.error("handle request error", e);
            response="server error:" + e.getMessage();
            return response;
        }
        return response;
    }

    private static Map<String, Object> getData(MultiMap multiMap) {
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, String> entry : multiMap.entries()) {
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }
}
