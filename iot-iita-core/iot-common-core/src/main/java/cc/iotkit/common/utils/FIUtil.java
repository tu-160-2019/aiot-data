package cc.iotkit.common.utils;

import cc.iotkit.common.function.IfHandler;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
public class FIUtil {


    public static IfHandler isTotF(boolean param) {
        return (tHandler, fHandler) -> {
            if (param) {
                tHandler.run();
            } else {
                fHandler.run();
            }
        };
    }
}
