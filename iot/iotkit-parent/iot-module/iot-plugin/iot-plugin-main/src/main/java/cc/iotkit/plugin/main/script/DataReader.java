package cc.iotkit.plugin.main.script;


import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import java.util.function.Consumer;

/**
 * 数据包读取器
 *
 * @author sjg
 */
public class DataReader {

    public static RecordParser getParser(Consumer<Buffer> receiveHandler) {
        RecordParser parser = RecordParser.newFixed(4);
        // 设置处理器
        parser.setOutput(new Handler<>() {
            // 表示当前数据长度
            int size = -1;

            @Override
            public void handle(Buffer buffer) {
                //-1表示当前还没有长度信息，需要从收到的数据中取出长度
                if (-1 == size) {
                    //取出长度
                    size = buffer.getInt(0);
                    //动态修改长度
                    parser.fixedSizeMode(size);
                } else {
                    //如果size != -1, 说明已经接受到长度信息了，接下来的数据就是protobuf可识别的字节数组
                    byte[] buf = buffer.getBytes();
                    receiveHandler.accept(Buffer.buffer(buf));
                    //处理完后要将长度改回
                    parser.fixedSizeMode(4);
                    //重置size变量
                    size = -1;
                }
            }
        });
        return parser;
    }
}
