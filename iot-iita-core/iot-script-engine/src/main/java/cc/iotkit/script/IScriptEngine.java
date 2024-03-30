package cc.iotkit.script;

import com.fasterxml.jackson.core.type.TypeReference;

public interface IScriptEngine {

    void setScript(String key);

    void putScriptEnv(String key, Object val);

    void invokeMethod(String methodName, Object... args);

    <T> T invokeMethod(TypeReference<T> type, String methodName, Object... args);

    String invokeMethod(String methodName, String args);

}
