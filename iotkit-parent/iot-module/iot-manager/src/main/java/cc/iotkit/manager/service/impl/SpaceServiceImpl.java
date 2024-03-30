package cc.iotkit.manager.service.impl;

import cc.iotkit.data.manager.ISpaceData;
import cc.iotkit.manager.service.ISpaceService;
import cc.iotkit.model.space.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/8/25 10:23
 */
@Service
public class SpaceServiceImpl implements ISpaceService {
    @Autowired
    private ISpaceData spaceData;

    @Override
    public Space save(Space space) {
        return spaceData.save(space);
    }

    @Override
    public List<Space> findByHomeId(Long homeId) {
        return spaceData.findByHomeId(homeId);
    }

    @Override
    public Space findById(Long id) {
        return spaceData.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        spaceData.deleteById(id);
    }
}
