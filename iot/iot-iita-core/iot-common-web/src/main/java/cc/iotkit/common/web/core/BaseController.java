package cc.iotkit.common.web.core;


import cc.iotkit.common.exception.ViewException;
import cc.iotkit.common.utils.StringUtils;

/**
 * web层通用数据处理
 *
 * @author Lion Li
 */
public class BaseController {

    public static void fail() {
        throw new ViewException("操作失败");
    }

    public static void fail(String msg) {
        throw new ViewException(ViewException.CODE_FAILED, msg);
    }

    public static <T> void fail(T data) {
        throw new ViewException(ViewException.CODE_FAILED, "操作失败", data);
    }

    public static <T> void fail(String msg, T data) {
        throw new ViewException(ViewException.CODE_FAILED, msg, data);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     */
    public static <T> void warn(String msg) {
        throw new ViewException(ViewException.CODE_WARN, msg);
    }

    /**
     * 返回警告消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     */
    public static <T> void warn(String msg, T data) {
        throw new ViewException(ViewException.CODE_WARN, msg, data);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }

}
