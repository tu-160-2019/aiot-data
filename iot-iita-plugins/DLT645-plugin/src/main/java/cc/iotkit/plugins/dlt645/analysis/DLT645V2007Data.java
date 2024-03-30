package cc.iotkit.plugins.dlt645.analysis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:58
 */
@Slf4j
@Setter
@Getter
public class DLT645V2007Data extends DLT645Data {
    /**
     * DI1/DI0
     */
    private byte di0 = 0;
    private byte di1 = 0;
    private byte di2 = 0;
    private byte di3 = 0;


    public String getKey() {
        String key = "";
        key += StringUtils.leftPad(Integer.toHexString(this.di3 & 0xFF),2,"0");
        key += StringUtils.leftPad(Integer.toHexString(this.di2 & 0xFF),2,"0");
        key += StringUtils.leftPad(Integer.toHexString(this.di1 & 0xFF),2,"0");
        key += StringUtils.leftPad(Integer.toHexString(this.di0 & 0xFF),2,"0") + "";
        return key.toUpperCase();
    }

    @Override
    public byte[] getDIn() {
        byte[] value = new byte[4];
        value[0] = this.di0;
        value[1] = this.di1;
        value[2] = this.di2;
        value[3] = this.di3;

        return value;
    }

    @Override
    public void setDIn(byte[] value) {
        if (value.length < 4) {
            log.info("DATA LENGTH ERROR");
        }

        this.di0 = value[0];
        this.di1 = value[1];
        this.di2 = value[2];
        this.di3 = value[3];
    }

    /**
     * 2007版的DIn 4字节
     *
     * @return
     */
    @Override
    public int getDInLen() {
        return 4;
    }
}
