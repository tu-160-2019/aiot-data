package net.srt.flink.process.exception;


import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.process.context.ProcessContextHolder;

public class FlinkProcessException extends RuntimeException {

    public FlinkProcessException() {
    }

    public FlinkProcessException(String message) {
        super(message);
        ProcessContextHolder.getProcess().error(message);
    }

    public FlinkProcessException(String message, Throwable cause) {
        super(message, cause);
        ProcessContextHolder.getProcess().error(LogUtil.getError(cause));
    }

    public FlinkProcessException(Throwable cause) {
        super(cause);
        ProcessContextHolder.getProcess().error(cause.toString());
    }

    public FlinkProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        ProcessContextHolder.getProcess().error(LogUtil.getError(cause));
    }
}
