package cc.iotkit.message.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sjg
 */
@Data
public class QyWechatConfig implements Serializable {
    private String qyWechatWebhook;
}
