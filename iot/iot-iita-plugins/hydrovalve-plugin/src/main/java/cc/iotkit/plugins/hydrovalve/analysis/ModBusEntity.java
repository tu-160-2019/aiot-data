package cc.iotkit.plugins.hydrovalve.analysis;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:44
 */
@Getter
@Setter
public class ModBusEntity {
    /**
     * 流水号
     */
    private int sn = 0;

    /**
     * 地址
     */
    private byte devAddr = 0x01;

    /**
     * 功能码
     */
    private byte func = 0x01;

    /**
     * 数据域
     */
    private byte[] data = new byte[0];

    /**
     * 出错信息
     */
    private int errCode = 0;

    /**
     * 出错信息
     */
    private String errMsg = "";
}
