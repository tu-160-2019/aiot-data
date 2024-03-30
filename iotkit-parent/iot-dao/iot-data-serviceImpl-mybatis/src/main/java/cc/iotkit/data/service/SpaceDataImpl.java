package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SpaceRepository;
import cc.iotkit.data.manager.ISpaceData;
import cc.iotkit.data.model.TbSpace;
import cc.iotkit.model.space.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class SpaceDataImpl implements ISpaceData, IJPACommData<Space, Long> {

    @Autowired
    private SpaceRepository spaceRepository;


    @Override
    public JpaRepository getBaseRepository() {
        return spaceRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSpace.class;
    }

    @Override
    public Class getTClass() {
        return Space.class;
    }

    @Override
    public List<Space> findByHomeId(Long homeId) {
        return MapstructUtils.convert(spaceRepository.findByHomeId(homeId), Space.class);
    }
}
