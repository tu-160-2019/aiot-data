package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbChannel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author: 石恒
 * date: 2023-05-11 17:51
 * description:
 **/
public interface ChannelRepository extends JpaRepository<TbChannel, String> {
}
