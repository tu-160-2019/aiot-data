package cc.iotkit.data.service;

import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.NotifyMessageRepository;
import cc.iotkit.data.manager.INotifyMessageData;
import cc.iotkit.data.model.TbNotifyMessage;
import cc.iotkit.model.notify.NotifyMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: 石恒
 * @Date: 2023/5/13 18:35
 * @Description:
 */
@Primary
@Service
public class NotifyMessageDataImpl implements INotifyMessageData, IJPACommData<NotifyMessage, Long> {

    @Resource
    private NotifyMessageRepository notifyMessageRepository;


    @Override
    public JpaRepository getBaseRepository() {
        return notifyMessageRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbNotifyMessage.class;
    }

    @Override
    public Class getTClass() {
        return NotifyMessage.class;
    }

}
