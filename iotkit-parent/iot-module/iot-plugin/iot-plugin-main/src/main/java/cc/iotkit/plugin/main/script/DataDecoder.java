package cc.iotkit.plugin.main.script;

import io.vertx.core.buffer.Buffer;
import lombok.AllArgsConstructor;
import lombok.Data;
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
        ReadField rf = new ReadField(0, buffer);

        data.setMid(rf.read());
        data.setPluginId(rf.read());
        data.setMethod(rf.read());
        data.setArgs(rf.read());
        data.setResult(rf.read());
        return data;
    }


    @Data
    @AllArgsConstructor
    private static class ReadField {

        private int idx = 0;

        private Buffer buffer;

        private String read() {
            int len = buffer.getInt(idx);
            idx += 4;
            String s = new String(buffer.getBytes(idx, idx + len));
            idx += len;
            return s;
        }

    }
}
