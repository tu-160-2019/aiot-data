package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbChannelTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 石恒
 * @Date: 2023/5/11 21:00
 * @Description:
 */
public interface ChannelTemplateRepository extends JpaRepository<TbChannelTemplate, String> {
}
