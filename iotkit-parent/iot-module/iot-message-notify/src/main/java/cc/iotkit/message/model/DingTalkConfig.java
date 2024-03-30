package cc.iotkit.message.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sjg
 */
@Data
public class DingTalkConfig implements Serializable {
    private String dingTalkWebhook;
}
