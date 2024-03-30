package cc.iotkit.plugins.tcp.parser;

import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据解码
 *
 * @author sjg
 */
@Slf4j
public class DataDecoder {

    public static DataPackage decode(Buffer buffer) {
        DataPackage data = new DataPackage();
        data.setAddr(buffer.getBuffer(0, 6).toString());
        data.setCode(buffer.getShort(6));
        data.setMid(buffer.getShort(8));
        data.setPayload(buffer.getBytes(10, buffer.length()));
        return data;
    }

}
