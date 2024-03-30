package cc.iotkit.message.listener;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.message.event.MessageEvent;
import cc.iotkit.message.model.EmailConfig;
import cc.iotkit.message.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * author: 石恒
 * date: 2023-05-08 15:09
 * description:
 **/
@Slf4j
@Component
public class EmailEventListener implements MessageEventListener {

    @Override
    @EventListener(classes = MessageEvent.class, condition = "#event.message.channel=='Email'")
    public void doEvent(MessageEvent event) {
        Message message = event.getMessage();
        String channelConfig = message.getChannelConfig();
        EmailConfig emailConfig = JsonUtils.parse(channelConfig, EmailConfig.class);

        JavaMailSenderImpl jms = new JavaMailSenderImpl();
        jms.setHost(emailConfig.getHost());
        jms.setUsername(emailConfig.getUserName());
        jms.setPassword(emailConfig.getPassWord());
        jms.setDefaultEncoding("utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", String.valueOf(null == emailConfig.getSmtpAuth() || emailConfig.getSmtpAuth()));
        jms.setJavaMailProperties(p);
        MimeMessage mimeMessage = jms.createMimeMessage();
        try {
            String content = message.getFormatContent();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            //收件人
            String[] split = emailConfig.getTo().split(",");
            messageHelper.setTo(split);
            //标题
            messageHelper.setSubject(content.substring(0, Math.min(20, content.length())));
            //内容
            messageHelper.setText(content, true);
            //发件人
            messageHelper.setFrom(emailConfig.getFrom());
            jms.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送邮件失败.", e);
        }
    }
}
