package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.common.api.Paging;

import cc.iotkit.data.manager.ISpaceData;
import cc.iotkit.data.model.TbSpace;
import cc.iotkit.model.space.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class SpaceDataImpl implements ISpaceData, IJPACommData<Space, Long, TbSpace> {

    @Autowired
    private SpaceService spaceService;


    public SpaceService getBaseRepository() {
        return spaceService;
    }


    public Class getJpaRepositoryClass() {
        return TbSpace.class;
    }


    public Class getTClass() {
        return Space.class;
    }

    public List<Space> findByHomeId(Long homeId) {
        List<TbSpace> spaceList = spaceService.findByHomeId(homeId);
        return MapstructUtils.convert(spaceList, Space.class);
    }


    public List<Space> findByUid(String uid) {
        List<TbSpace> spaceList = spaceService.findByUid(uid);
        return MapstructUtils.convert(spaceList, Space.class);
    }

    public Paging<Space> findByUid(String uid, int page, int size) {
        Page<TbSpace> tbSpacePage = spaceService.findByUid(uid, page, size);
        Paging<Space> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(tbSpacePage.getRecords(), Space.class));
        paging.setTotal(tbSpacePage.getTotal());
        return paging;
    }

    public List<Space> findByUidOrderByCreateAtDesc(String uid) {
        List<TbSpace> spaceList = spaceService.findByUidOrderByCreateAtDesc(uid);
        return MapstructUtils.convert(spaceList, Space.class);
    }

    public List<Space> findByUidAndHomeIdOrderByCreateAtDesc(String uid, String homeId) {
        List<TbSpace> spaceList = spaceService.findByUidAndHomeIdOrderByCreateAtDesc(uid, homeId);
        return MapstructUtils.convert(spaceList, Space.class);
    }
}
