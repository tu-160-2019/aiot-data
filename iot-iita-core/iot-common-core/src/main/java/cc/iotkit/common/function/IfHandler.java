package cc.iotkit.common.function;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
@FunctionalInterface
public interface IfHandler {

    void handler(Runnable tHandler, Runnable fHandler);
}
