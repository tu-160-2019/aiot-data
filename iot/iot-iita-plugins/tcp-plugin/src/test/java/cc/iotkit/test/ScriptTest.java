package cc.iotkit.test;

import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.script.IScriptEngine;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ScriptTest {
    IScriptEngine scriptEngine;

    @Before
    public void init() {
        scriptEngine = new LocalPluginScript("script.js").getScriptEngine("");
    }

    @Test
    public void testEncode() {
        String rst = scriptEngine.invokeMethod(new TypeReference<>() {
        }, "encode", Map.of("powerstate", 1));
        System.out.println(rst);
    }

}
