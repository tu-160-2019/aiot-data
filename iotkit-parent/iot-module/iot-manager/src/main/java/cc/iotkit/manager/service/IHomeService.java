package cc.iotkit.manager.service;

import cc.iotkit.manager.dto.bo.space.HomeBo;
import cc.iotkit.model.space.Home;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 10:21
 */
public interface IHomeService {

    Home save(HomeBo home);

    Home findByUserIdAndCurrent(Long userId, boolean current);

    List<Home> findByUserId(Long userId);

    Home findById(Long id);

    boolean checkHomeNameUnique(HomeBo user);

    void deleteById(Long id);

    void changCurrentHome(HomeBo home);
}
