package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.ReflectUtil;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.data.dao.*;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.model.*;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.product.Category;
import cc.iotkit.model.product.Product;
import cc.iotkit.model.stats.DataItem;
import cc.iotkit.model.system.SysDept;
import cc.iotkit.model.system.SysUser;
import cn.hutool.core.util.ObjectUtil;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cc.iotkit.data.model.QTbDeviceGroupMapping.tbDeviceGroupMapping;
import static cc.iotkit.data.model.QTbDeviceInfo.tbDeviceInfo;
import static cc.iotkit.data.model.QTbDeviceSubUser.tbDeviceSubUser;
import static cc.iotkit.data.model.QTbProduct.tbProduct;
import static cc.iotkit.data.model.QTbSysDept.tbSysDept;
import static cc.iotkit.data.model.QTbSysUser.tbSysUser;

@Primary
@Service
@RequiredArgsConstructor
public class DeviceInfoDataImpl implements IDeviceInfoData, IJPACommData<DeviceInfo, String> {


    private final DeviceInfoRepository deviceInfoRepository;

    private final DeviceSubUserRepository deviceSubUserRepository;

    private final DeviceGroupMappingRepository deviceGroupMappingRepository;

    private final DeviceGroupRepository deviceGroupRepository;

    private final DeviceTagRepository deviceTagRepository;

    @Qualifier("productDataCache")
    private final IProductData productData;

    @Qualifier("categoryDataCache")
    private final ICategoryData categoryData;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return deviceInfoRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceInfo.class;
    }

    @Override
    public Class getTClass() {
        return DeviceInfo.class;
    }

    @Override
    public void saveProperties(String deviceId, Map<String, DevicePropertyCache> properties) {
    }

    @Override
    public Map<String, DevicePropertyCache> getProperties(String deviceId) {
        return new HashMap<>();
    }

    @Override
    public long getPropertyUpdateTime(String deviceId) {
        return 0;
    }

    @Override
    public DeviceInfo findByDeviceId(String deviceId) {
        TbDeviceInfo tbDeviceInfo = deviceInfoRepository.findByDeviceId(deviceId);

        DeviceInfo dto = MapstructUtils.convert(tbDeviceInfo, DeviceInfo.class);
        fillDeviceInfo(deviceId, tbDeviceInfo, dto);
        return dto;
    }

    /**
     * 填充设备其它信息
     */
    private DeviceInfo fillDeviceInfo(String deviceId, TbDeviceInfo vo, DeviceInfo dto) {
        if (vo == null || dto == null) {
            return null;
        }
        //取子关联用户
        dto.setSubUid(deviceSubUserRepository.findByDeviceId(deviceId).stream()
                .map(TbDeviceSubUser::getUid).collect(Collectors.toList()));

        //取设备所属分组
        List<TbDeviceGroupMapping> groupMappings = deviceGroupMappingRepository.findByDeviceId(deviceId);
        Map<String, DeviceInfo.Group> groups = new HashMap<>();
        for (TbDeviceGroupMapping mapping : groupMappings) {
            TbDeviceGroup deviceGroup = deviceGroupRepository.findById(mapping.getGroupId()).orElse(null);
            if (deviceGroup == null) {
                continue;
            }
            groups.put(deviceGroup.getId(), new DeviceInfo.Group(deviceGroup.getId(), deviceGroup.getName()));
        }
        dto.setGroup(groups);

        //取设备标签
        List<TbDeviceTag> deviceTags = deviceTagRepository.findByDeviceId(deviceId);
        Map<String, DeviceInfo.Tag> tagMap = new HashMap<>();
        for (TbDeviceTag tag : deviceTags) {
            tagMap.put(tag.getCode(), new DeviceInfo.Tag(tag.getCode(), tag.getName(), tag.getValue()));
        }
        dto.setTag(tagMap);

        //将设备状态从vo转为dto的
        parseStateToDto(vo, dto);
        return dto;
    }

    /**
     * 将设备状态从vo转为dto的
     */
    private void parseStateToDto(TbDeviceInfo vo, DeviceInfo dto) {
        dto.setState(new DeviceInfo.State("online".equals(vo.getState()),
                vo.getOnlineTime(), vo.getOfflineTime()));
        dto.setLocate(new DeviceInfo.Locate(vo.getLongitude(), vo.getLatitude()));
    }

    /**
     * 将设备状态从dto转vo
     */
    private void parseStateToVo(DeviceInfo dto, TbDeviceInfo vo) {
        DeviceInfo.State state = dto.getState();
        vo.setState(state != null && state.isOnline() ? "online" : "offline");
        vo.setOfflineTime(state != null ? state.getOfflineTime() : null);
        vo.setOnlineTime(state != null ? state.getOnlineTime() : null);
        DeviceInfo.Locate locate = dto.getLocate();
        vo.setLongitude(locate.getLongitude());
        vo.setLatitude(locate.getLatitude());
    }

    /**
     * 将数据库中查出来的vo转为dto
     */
    private DeviceInfo parseVoToDto(TbDeviceInfo vo) {
        if (vo == null) {
            return null;
        }
        DeviceInfo dto = MapstructUtils.convert(vo, DeviceInfo.class);

        fillDeviceInfo(vo.getDeviceId(), vo, dto);
        return dto;
    }

    /**
     * 将数据库中查出来的vo列表转为dto列表
     */
    private List<DeviceInfo> parseVoToDto(List<TbDeviceInfo> vos) {
        return vos.stream().map(d -> {

            DeviceInfo dto = MapstructUtils.convert(d, DeviceInfo.class);

            fillDeviceInfo(d.getDeviceId(), d, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public DeviceInfo findByDeviceName(String deviceName) {
        return parseVoToDto(deviceInfoRepository.findByDeviceName(deviceName));
    }

    @Override
    public List<DeviceInfo> findByParentId(String parentId) {
        return parseVoToDto(deviceInfoRepository.findByParentId(parentId));
    }

    @Override
    public List<String> findSubDeviceIds(String parentId) {
        return jpaQueryFactory.select(tbDeviceInfo.deviceId).from(tbDeviceInfo)
                .where(tbDeviceInfo.parentId.eq(parentId)).fetch();
    }

    @Override
    public List<DeviceInfo> findByProductNodeType(String uid) {
        List<TbDeviceInfo> devices = jpaQueryFactory.select(tbDeviceInfo).from(tbDeviceInfo)
                .join(tbProduct).on(tbProduct.nodeType.eq(0).and(tbDeviceInfo.productKey.eq(tbProduct.productKey))).fetch();
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    @Override
    public boolean existByProductKey(String productKey) {
        return Optional.ofNullable(jpaQueryFactory.selectOne().from(tbDeviceInfo).where(tbDeviceInfo.productKey.eq(productKey)).fetchOne()).orElse(0) > 0;
    }

    @Override
    public List<DeviceInfo> findNeverUsedDevices() {
        JPAQuery<TbDeviceInfo> query = jpaQueryFactory.selectFrom(tbDeviceInfo);
        query.where(tbDeviceInfo.onlineTime.isNull());
        List<TbDeviceInfo> devices = query.fetch();
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    @Override
    public Paging<DeviceInfo> findByConditions(String uid, String subUid,
                                               String productKey, String groupId,
                                               Boolean online, String keyword,
                                               int page, int size) {
        JPAQuery<TbDeviceInfo> query = jpaQueryFactory.selectFrom(tbDeviceInfo);

        // 根据groupId, 如果groupId存在，则关联查询TbDeviceGroupMapping, 根据groupId,查询对应的devices
        if (StringUtils.isNotBlank(groupId)) {
            query.join(tbDeviceGroupMapping).on(tbDeviceGroupMapping.deviceId.eq(tbDeviceInfo.deviceId));
            query.where(tbDeviceGroupMapping.groupId.eq(groupId));
        }

        if (StringUtils.isNotBlank(uid)) {
            query.where(tbDeviceInfo.uid.eq(uid));
        }

        if (StringUtils.isNotBlank(subUid)) {
            query.join(tbDeviceSubUser).on(tbDeviceSubUser.deviceId.eq(tbDeviceInfo.deviceId));
            query.where(tbDeviceSubUser.uid.eq(subUid));
        }

        if (StringUtils.isNotBlank(productKey)) {
            query.where(tbDeviceInfo.productKey.eq(productKey));
        }

        if (online != null) {
            query.where(tbDeviceInfo.state.eq(online ? "online" : "offline"));
        }

        if (StringUtils.isNotBlank(keyword)) {
            query.where(tbDeviceInfo.deviceId.like("%" + keyword + "%")
                    .or(tbDeviceInfo.deviceName.like("%" + keyword + "%")));
        }

        query.orderBy(tbDeviceInfo.createAt.desc());
        query.offset((page - 1) * size).limit(size);

        List<TbDeviceInfo> tbDeviceInfos = query.fetch();
        long total = query.fetchCount();
        List<DeviceInfo> deviceInfos = new ArrayList<>(tbDeviceInfos.size());
        for (TbDeviceInfo tbDeviceInfo : tbDeviceInfos) {
            DeviceInfo deviceInfo = MapstructUtils.convert(tbDeviceInfo, DeviceInfo.class);
            fillDeviceInfo(tbDeviceInfo.getDeviceId(), tbDeviceInfo, deviceInfo);
            deviceInfos.add(deviceInfo);
        }
        return new Paging<>(total, deviceInfos);
    }


    @Override
    public void updateTag(String deviceId, DeviceInfo.Tag tag) {
        TbDeviceTag deviceTag = deviceTagRepository.findByDeviceIdAndCode(deviceId, tag.getId());
        if (deviceTag != null) {
            deviceTag.setName(tag.getName());
            deviceTag.setValue(tag.getValue());
            deviceTagRepository.save(deviceTag);
        } else {
            deviceTagRepository.save(
                    TbDeviceTag.builder()
                            .id(UUID.randomUUID().toString())
                            .code(tag.getId())
                            .deviceId(deviceId)
                            .name(tag.getName())
                            .value(tag.getValue())
                            .build()
            );
        }
    }

    @Override
    public List<DataItem> getDeviceStatsByCategory(String uid) {
        //先按产品统计设备数量
        JPAQuery<DataItem> query = jpaQueryFactory.select(Projections.bean(DataItem.class,
                        tbDeviceInfo.productKey.as("name"),
                        tbDeviceInfo.productKey.count().as("value")))
                .from(tbDeviceInfo)
                .groupBy(tbDeviceInfo.productKey);

        if (StringUtils.isNotBlank(uid)) {
            query.where(tbDeviceInfo.uid.eq(uid));
        }

        List<DataItem> stats = new ArrayList<>();

        List<DataItem> rst = query.fetch();
        for (DataItem item : rst) {
            //找到产品对应的品类取出品类名
            Product product = productData.findByProductKey(item.getName());
            String cateId = product.getCategory();
            Category category = categoryData.findById(cateId);
            if (category == null) {
                continue;
            }
            //将数据替换成按品类的数据
            item.setName(category.getName());
        }

        //按品类分组求合
        rst.stream().collect(Collectors.groupingBy(DataItem::getName,
                        Collectors.summarizingLong(item -> (long) item.getValue())))
                .forEach((key, sum) -> stats.add(new DataItem(key, sum.getSum())));

        return stats;
    }

    @Override
    public long countByGroupId(String groupId) {
        return deviceGroupMappingRepository.countByGroupId(groupId);
    }

    @Override
    @Transactional
    public void addToGroup(String deviceId, DeviceInfo.Group group) {
        String groupId = UUID.randomUUID().toString();
        deviceGroupMappingRepository.save(new TbDeviceGroupMapping(groupId, deviceId, group.getId()));

        //更新设备数量
        updateGroupDeviceCount(groupId);
    }

    private void updateGroupDeviceCount(String groupId) {
        //更新设备数量
        TbDeviceGroup deviceGroup = deviceGroupRepository.findById(groupId).orElse(null);
        if (deviceGroup != null) {
            deviceGroup.setDeviceQty((int) countByGroupId(groupId));
            deviceGroupRepository.save(deviceGroup);
        }
    }

    @Override
    public void updateGroup(String groupId, DeviceInfo.Group group) {
        //更新设备信息中的分组信息，关系数据库中不需要实现
    }

    @Override
    @Transactional
    public void removeGroup(String deviceId, String groupId) {
        jpaQueryFactory.delete(tbDeviceGroupMapping)
                .where(tbDeviceGroupMapping.deviceId.eq(deviceId)
                        .and(tbDeviceGroupMapping.groupId.eq(groupId)))
                .execute();
        //更新设备数量
        updateGroupDeviceCount(groupId);
    }

    @Override
    @Transactional
    public void removeGroup(String groupId) {
        jpaQueryFactory.delete(tbDeviceGroupMapping)
                .where(tbDeviceGroupMapping.groupId.eq(groupId))
                .execute();
        //更新设备数量
        updateGroupDeviceCount(groupId);
    }

    @Override
    public List<DeviceInfo> findByUid(String uid) {
        return new ArrayList<>();
    }

    @Override
    public Paging<DeviceInfo> findByUid(String uid, int page, int size) {
        return new Paging<>();
    }

    @Override
    public long countByUid(String uid) {
        return 0;
    }


    @Override
    public DeviceInfo findById(String s) {
        return MapstructUtils.convert(
                deviceInfoRepository.findById(s).orElse(null), DeviceInfo.class);
    }

    @Override
    public List<DeviceInfo> findByIds(Collection<String> ids) {
        return MapstructUtils.convert(deviceInfoRepository.findAllById(ids), DeviceInfo.class);
    }

    @Override
    @Transactional
    public DeviceInfo save(DeviceInfo data) {
        TbDeviceInfo vo = deviceInfoRepository.findByDeviceId(data.getDeviceId());
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
        }
        if (vo == null) {
            vo = new TbDeviceInfo();
        }

        ReflectUtil.copyNoNulls(data, vo);
        //状态转换
        parseStateToVo(data, vo);
        //保存设备信息
        deviceInfoRepository.save(vo);

        //设备分组转换
        Map<String, DeviceInfo.Group> groupMap = data.getGroup();
        groupMap.forEach((id, group) -> {
            TbDeviceGroupMapping mapping = deviceGroupMappingRepository.findByDeviceIdAndGroupId(data.getDeviceId(), id);
            if (mapping == null) {
                //保存设备分组与设备对应关系
                deviceGroupMappingRepository.save(new TbDeviceGroupMapping(
                        UUID.randomUUID().toString(),
                        data.getDeviceId(),
                        id
                ));
            }
        });

        return data;
    }

    @Override
    public void batchSave(List<DeviceInfo> data) {

    }

    @Override
    public void deleteById(String s) {
        deviceInfoRepository.deleteById(s);
    }

    @Override
    public void deleteByIds(Collection<String> ids) {
        deviceInfoRepository.deleteAllById(ids);
    }


    @Override
    public long count() {
        return deviceInfoRepository.count();
    }

    @Override
    public Paging<DeviceInfo> findAll(PageRequest<DeviceInfo> pageRequest) {
        Page<TbDeviceInfo> ret = deviceInfoRepository.findAll(PageBuilder.toPageable(pageRequest));
        List<DeviceInfo> list = new ArrayList<>();
        for (TbDeviceInfo deviceInfo : ret.getContent()) {
            list.add(fillDeviceInfo(deviceInfo.getDeviceId(), deviceInfo, MapstructUtils.convert(deviceInfo, DeviceInfo.class)));
        }
        return new Paging<>(ret.getTotalElements(), list);
    }

    @Override
    public DeviceInfo findOneByCondition(DeviceInfo data) {
        return null;
    }

    @Override
    public List<DeviceInfo> findAllByCondition(DeviceInfo data) {
        return buildQuery(buildQueryCondition(data));
    }

    private List<DeviceInfo> buildQuery(Predicate predicate) {
        List<TbDeviceInfo> devices = jpaQueryFactory.select(Projections.bean(TbDeviceInfo.class,
                tbDeviceInfo.deviceId, tbDeviceInfo.deviceName, tbDeviceInfo.state ))
                .from(tbDeviceInfo)
                .where(predicate).fetch();
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    private Predicate buildQueryCondition(DeviceInfo device) {
        return PredicateBuilder.instance()
                .and(ObjectUtil.isNotNull(device.getId()), () -> tbDeviceInfo.id.eq(device.getId()))
                .and(ObjectUtil.isNotNull(device.getState().isOnline()), () -> tbDeviceInfo.state.eq(device.getState().isOnline() ? "online" : "offline"))
                .build();
    }

}
