package cc.iotkit.plugins.tcp.parser;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 数据包
 *
 * @author sjg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataPackage {

    public static final short CODE_REGISTER = 0x10;
    public static final short CODE_REGISTER_REPLY = 0x11;
    public static final short CODE_HEARTBEAT = 0x20;
    public static final short CODE_DATA_UP = 0x30;
    public static final short CODE_DATA_DOWN = 0x40;

    /**
     * 设备地址
     */
    private String addr;

    /**
     * 功能码
     */
    private short code;

    /**
     * 消息序号
     */
    private short mid;

    /**
     * 包体数据
     */
    @JsonSerialize(using = BufferSerializer.class)
    private byte[] payload;


    public static class BufferSerializer extends JsonSerializer<byte[]> {

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(HexUtil.encodeHexStr(value));
        }
    }
}
