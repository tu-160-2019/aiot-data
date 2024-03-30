package cc.iotkit.common.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视图异常
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ViewException extends RuntimeException {

    public static final int CODE_FAILED = 500;
    public static final int CODE_WARN = 601;

    private int code;
    private String message;
    private Object data;

    public ViewException() {
    }

    public ViewException(String message) {
        super(message);
        this.message = message;
    }

    public ViewException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ViewException(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
