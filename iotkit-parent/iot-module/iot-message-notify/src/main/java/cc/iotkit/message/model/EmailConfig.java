package cc.iotkit.message.model;

import lombok.Data;

import java.io.Serializable;

/**
 * author: 石恒
 * date: 2023-05-08 15:58
 * description:
 **/
@Data
public class EmailConfig implements Serializable {
    private String host;
    private String userName;
    private String passWord;
    private String from;
    private String to;
    private String title;
    private String content;
    private Boolean smtpAuth;
}
