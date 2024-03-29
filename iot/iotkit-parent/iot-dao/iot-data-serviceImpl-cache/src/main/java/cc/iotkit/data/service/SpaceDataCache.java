package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.data.cache.SpaceCacheEvict;
import cc.iotkit.data.manager.ISpaceData;
import cc.iotkit.model.space.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Qualifier("spaceDataCache")
public class SpaceDataCache implements ISpaceData {

    @Autowired
    private ISpaceData spaceData;
    @Autowired
    private SpaceCacheEvict spaceCacheEvict;

    @Override
    public List<Space> findByHomeId(Long homeId) {
        return spaceData.findByHomeId(homeId);
    }

    @Override
    @Cacheable(value = Constants.CACHE_SPACE, key = "#root.method.name+#s", unless = "#result == null")
    public Space findById(Long s) {
        return spaceData.findById(s);
    }

    @Override
    public List<Space> findByIds(Collection<Long> id) {
        return null;
    }

    @Override
    public Space save(Space data) {
        data = spaceData.save(data);
        spaceCacheEvict.findById(data.getId());
        return data;
    }

    @Override
    public void batchSave(List<Space> data) {

    }

    @Override
    public void deleteById(Long s) {
        spaceData.deleteById(s);
    }

    @Override
    public void deleteByIds(Collection<Long> strings) {

    }

    @Override
    public long count() {
        return spaceData.count();
    }

    @Override
    public List<Space> findAll() {
        return spaceData.findAll();
    }

    @Override
    public Paging<Space> findAll(PageRequest<Space> pageRequest) {
        return spaceData.findAll(pageRequest);
    }

    @Override
    public List<Space> findAllByCondition(Space data) {
        return null;
    }

    @Override
    public Space findOneByCondition(Space data) {
        return null;
    }

}
