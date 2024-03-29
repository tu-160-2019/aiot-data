package cc.iotkit.plugins.hydrovalve.analysis;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:48
 */
public class ModBusError {
    static final String err01 = "err=01:非法的功能码";
    static final String err02 = "err=02:非法的数据地址";
    static final String err03 = "err=03:非法的数据值";
    static final String err04 = "err=04:服务器故障";
    static final String err05 = "err=05:确认。";
    static final String err06 = "err=06:服务器繁忙";
    static final String err10 = "err=10:网关故障:网关路经是无效的";
    static final String err11 = "err=11:网关故障:目标设备没有响应";

    /**
     * 获取出错信息
     * @param code 出错代码
     * @return 出错信息
     */
    static String getError(int code) {
        if (code == 1) {
            return err01;
        }
        if (code == 2) {
            return err02;
        }
        if (code == 3) {
            return err03;
        }
        if (code == 4) {
            return err04;
        }
        if (code == 5) {
            return err05;
        }
        if (code == 6) {
            return err06;
        }
        if (code == 10) {
            return err10;
        }
        if (code == 11) {
            return err11;
        }

        return "";
    }
}
