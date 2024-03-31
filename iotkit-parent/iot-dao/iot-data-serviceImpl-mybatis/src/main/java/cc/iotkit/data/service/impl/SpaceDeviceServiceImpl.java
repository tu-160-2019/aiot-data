package cc.iotkit.data.service.impl;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.SpaceDeviceMapper;
import cc.iotkit.data.model.TbSpaceDevice;
import cc.iotkit.data.service.SpaceDeviceService;
import cc.iotkit.model.space.SpaceDevice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("DBSpaceDeviceServiceImpl")
@Primary
public class SpaceDeviceServiceImpl extends ServiceImpl<SpaceDeviceMapper, TbSpaceDevice> implements SpaceDeviceService {
    @Override
    public List<SpaceDevice> findByUidOrderByUseAtDesc(String uid) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getTenantId, uid).orderByDesc(TbSpaceDevice::getUpdateTime));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByHomeIdAndCollect(Long homeId, boolean collect) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getHomeId, homeId)
                .eq(TbSpaceDevice::getCollect, collect));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByHomeId(Long homeId) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getHomeId, homeId));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findBySpaceId(Long spaceId) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getSpaceId, spaceId));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByUidOrderByAddAtDesc(String uid) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getTenantId, uid).orderByDesc(TbSpaceDevice::getCreateTime));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findBySpaceIdOrderByAddAtDesc(String spaceId) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getSpaceId, spaceId).orderByDesc(TbSpaceDevice::getCreateTime));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByUidAndSpaceIdOrderByAddAtDesc(String uid, String spaceId) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getTenantId, uid)
                .eq(TbSpaceDevice::getSpaceId, spaceId)
                .orderByDesc(TbSpaceDevice::getCreateTime));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public SpaceDevice findByDeviceId(String deviceId) {
        TbSpaceDevice tbSpaceDevice = this.baseMapper.selectOne(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getDeviceId, deviceId));
        return MapstructUtils.convert(tbSpaceDevice, SpaceDevice.class);
    }

    @Override
    public SpaceDevice findByDeviceIdAndUid(String deviceId, String uid) {
        TbSpaceDevice tbSpaceDevice = this.baseMapper.selectOne(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getDeviceId, deviceId)
                .eq(TbSpaceDevice::getTenantId, uid));
        return MapstructUtils.convert(tbSpaceDevice, SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByUid(String uid) {
        List<TbSpaceDevice> tbSpaceDeviceList = this.baseMapper.selectList(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getTenantId, uid));
        return MapstructUtils.convert(tbSpaceDeviceList, SpaceDevice.class);
    }

    @Override
    public Paging<SpaceDevice> findByUid(String uid, int page, int size) {
        Page<TbSpaceDevice> rowPage = new Page<>(page, size);
        Page<TbSpaceDevice> tbSpaceDevicePage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getTenantId, uid));
        Paging<SpaceDevice> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(tbSpaceDevicePage.getRecords(), SpaceDevice.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }

    public void deleteAllBySpaceId(Long spaceId) {
        this.baseMapper.delete(new LambdaQueryWrapper<TbSpaceDevice>()
                .eq(TbSpaceDevice::getSpaceId, spaceId));
    }
}
