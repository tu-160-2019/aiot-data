package cc.iotkit.plugin.main.script;

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

        Buffer body = Buffer.buffer();

        append(data.getMid(), body);

        append(data.getPluginId(), body);

        append(data.getMethod(), body);

        append(data.getArgs(), body);

        append(data.getResult(), body);

        Buffer buffer = Buffer.buffer();

        byte[] bytes = body.getBytes();
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);

        return buffer;
    }

    private static void append(String s, Buffer buffer) {
        byte[] bytes = s == null ? new byte[]{} : s.getBytes();
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }
}
