package srt.cloud.framework.dbswitch.core.util;

import com.github.freakchick.orange.engine.DynamicSqlEngine;

public class SqlEngineUtil {

    public static final DynamicSqlEngine ENGINE = new DynamicSqlEngine();

    public static DynamicSqlEngine getEngine() {
        return ENGINE;
    }
}
