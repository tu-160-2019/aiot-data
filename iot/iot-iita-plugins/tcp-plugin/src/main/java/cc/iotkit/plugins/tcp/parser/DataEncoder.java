package cc.iotkit.plugins.tcp.parser;

import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据编码
 *
 * @author sjg
 */
@Slf4j
public class DataEncoder {

    public static Buffer encode(DataPackage data) {
        Buffer buffer = Buffer.buffer();
        //设备地址(6byte) + 功能码(2byte) +	消息序号(2byte) + 包体(不定长度)
        buffer.appendInt(6+2+2+data.getPayload().length);
        buffer.appendBytes(data.getAddr().getBytes());
        buffer.appendShort(data.getCode());
        buffer.appendShort(data.getMid());
        buffer.appendBytes(data.getPayload());
        return buffer;
    }
}
