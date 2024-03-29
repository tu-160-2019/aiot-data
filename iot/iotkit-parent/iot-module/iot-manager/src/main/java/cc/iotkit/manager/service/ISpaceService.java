package cc.iotkit.manager.service;

import cc.iotkit.model.space.Space;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 10:22
 */
public interface ISpaceService {

    Space save(Space space);

    List<Space> findByHomeId(Long homeId);

    Space findById(Long id);

    void deleteById(Long id);
}
