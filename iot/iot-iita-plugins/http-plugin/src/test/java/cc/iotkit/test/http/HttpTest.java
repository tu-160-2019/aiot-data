package cc.iotkit.test.http;

import cc.iotkit.common.utils.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpTest {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor timer = ThreadUtil.newScheduled(1, "http-test");
        timer.scheduleWithFixedDelay(HttpTest::report, 0, 3, TimeUnit.SECONDS);
    }

    public static void report() {
        HttpResponse rst = HttpUtil.createPost("http://127.0.0.1:9084/sys/cGCrkK7Ex4FESAwe/cz00001/properties")
                .header("secret", "mBCr3TKstTj2KeM6")
                .body(new JsonObject()
                        .put("id", IdUtil.fastSimpleUUID())
                        .put("params", new JsonObject()
                                .put("powerstate", RandomUtil.randomInt(0, 2))
                                .put("rssi", RandomUtil.randomInt(-127, 127))
                                .getMap()
                        ).encode()
                ).execute();
        log.info("send result:status={},body={}", rst.getStatus(), rst.body());
    }

}
