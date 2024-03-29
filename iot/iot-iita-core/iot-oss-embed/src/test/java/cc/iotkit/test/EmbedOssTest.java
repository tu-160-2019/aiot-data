package cc.iotkit.test;

import cc.iotkit.common.oss.core.OssClient;
import cc.iotkit.common.oss.entity.UploadResult;
import cc.iotkit.common.oss.properties.OssProperties;
import cn.hutool.core.io.FileUtil;

public class EmbedOssTest {

    public static void main(String[] args) {
        OssClient ossClient = new OssClient("", OssProperties.builder()
                .endpoint(String.format("localhost:%d/iot-oss", 8086))
                .domain("")
                .secretKey("admin")
                .accessKey("123")
                .bucketName("iot")
                .accessPolicy("1")
                .region("local")
                .build());
        UploadResult upload = ossClient.upload(FileUtil.readBytes("/Users/sjg/Downloads/b1a35946f341db92402f09529c5e7c9c.jpg"), "13%2Fimg2.png", "image/jpeg");
        System.out.println(upload);

        String privateUrl = ossClient.getPrivateUrl("img2.png", 10);
        System.out.println(privateUrl);
    }

}
