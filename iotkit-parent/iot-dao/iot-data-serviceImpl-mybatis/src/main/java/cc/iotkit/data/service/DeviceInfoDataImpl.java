package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.ReflectUtil;

import cc.iotkit.data.mapper.*;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.model.*;

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.product.Category;
import cc.iotkit.model.product.Product;
import cc.iotkit.model.stats.DataItem;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class DeviceInfoDataImpl implements IDeviceInfoData, IJPACommData<DeviceInfo, String> {
//public class DeviceInfoDataImpl implements IDeviceInfoData, IJPACommData<DeviceInfo, String, TbDeviceInfo> {


    private final DeviceInfoService deviceInfoService;

    private final DeviceSubUserService deviceSubUserService;

    private final DeviceGroupMappingService deviceGroupMappingService;

    private final DeviceGroupService deviceGroupService;

    private final DeviceTagService deviceTagService;

    @Qualifier("productDataCache")
    private final IProductData productData;

    @Qualifier("categoryDataCache")
    private final ICategoryData categoryData;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public DeviceInfoService getBaseRepository() {
        return deviceInfoService;
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

        TbDeviceInfo tbDeviceInfo = deviceInfoService.findOneByConditions(new LambdaQueryWrapper<TbDeviceInfo>()
                .eq(TbDeviceInfo::getDeviceId, deviceId));
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
        dto.setSubUid(deviceSubUserService.findByConditions(new LambdaQueryWrapper<TbDeviceSubUser>().eq(TbDeviceSubUser::getDeviceId, deviceId)).stream()
                .map(TbDeviceSubUser::getUid).collect(Collectors.toList()));

        //取设备所属分组
        List<TbDeviceGroupMapping> groupMappings = deviceGroupMappingService.findByConditions(new LambdaQueryWrapper<TbDeviceGroupMapping>()
                .eq(TbDeviceGroupMapping::getDeviceId, deviceId));

        Map<String, DeviceInfo.Group> groups = new HashMap<>();
        for (TbDeviceGroupMapping mapping : groupMappings) {
            TbDeviceGroup deviceGroup = deviceGroupService.getById(mapping.getGroupId());
            if (deviceGroup == null) {
                continue;
            }
            groups.put(deviceGroup.getId(), new DeviceInfo.Group(deviceGroup.getId(), deviceGroup.getName()));
        }
        dto.setGroup(groups);

        //取设备标签
        List<TbDeviceTag> deviceTags = deviceTagService.findByConditions(new LambdaQueryWrapper<TbDeviceTag>()
                .eq(TbDeviceTag::getDeviceId, deviceId));
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
        return parseVoToDto(deviceInfoService.findOneByConditions(new LambdaQueryWrapper<TbDeviceInfo>().eq(TbDeviceInfo::getDeviceName, deviceName)));
    }

    @Override
    public List<DeviceInfo> findByParentId(String parentId) {
        return parseVoToDto(deviceInfoService.findByConditions(new LambdaQueryWrapper<TbDeviceInfo>().eq(TbDeviceInfo::getParentId, parentId)));
    }

    @Override
    public List<String> findSubDeviceIds(String parentId) {
        return jdbcTemplate.queryForList(
                "select device_id from device_info where parent_id=?", String.class, parentId);
    }

    public List<DeviceInfo> findByProductNodeType(String uid, Integer nodeType) {
        List<TbDeviceInfo> devices = deviceInfoService.findByProductNodeType(uid, nodeType);
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    public List<DeviceInfo> findByProductNodeType(String uid) {
        List<TbDeviceInfo> devices = deviceInfoService.findByProductNodeType(uid, 0);
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    @Override
    public boolean existByProductKey(String productKey) {
        TbDeviceInfo deviceInfo = deviceInfoService.findOneByConditions(new LambdaQueryWrapper<TbDeviceInfo>().eq(TbDeviceInfo::getProductKey, productKey));
        return Objects.nonNull(deviceInfo);
    }

    @Override
    public List<DeviceInfo> findNeverUsedDevices() {
        List<TbDeviceInfo> devices = deviceInfoService.findByConditions(new LambdaQueryWrapper<TbDeviceInfo>().eq(TbDeviceInfo::getOnlineTime, null));
        return MapstructUtils.convert(devices, DeviceInfo.class);
    }

    @Override
    public Paging<DeviceInfo> findByConditions(String uid, String subUid,
                                               String productKey, String groupId,
                                               Boolean online, String keyword,
                                               int page, int size) {
        String sql = "SELECT\n" +
                "a.id,\n" +
                "a.device_id,\n" +
                "a.product_key,\n" +
                "a.device_name,\n" +
                "a.model,\n" +
                "a.secret,\n" +
                "a.parent_id,\n" +
                "a.longitude,\n" +
                "a.latitude,\n" +
                "a.uid,\n" +
                "a.state,\n" +
                "a.online_time,\n" +
                "a.offline_time,\n" +
                "a.create_at\n" +
                "FROM device_info a ";

        if (StringUtils.isNotBlank(groupId)) {
            sql += " JOIN device_group_mapping b  on a.device_id=b.device_id\n" +
                    " JOIN device_group c on b.group_id=c.id ";
        }
        if (StringUtils.isNotBlank(subUid)) {
            sql += " JOIN device_sub_user d on d.device_id=a.device_id ";
        }

        List<Object> args = new ArrayList<>();
        sql += " where 1=1 ";
        if (StringUtils.isNotBlank(groupId)) {
            sql += "and c.id=? ";
            args.add(groupId);
        }

        if (StringUtils.isNotBlank(subUid)) {
            sql += "and d.uid=? ";
            args.add(subUid);
        } else if (StringUtils.isNotBlank(uid)) {
            sql += "and a.uid=? ";
            args.add(uid);
        }

        if (StringUtils.isNotBlank(productKey)) {
            sql += "and a.product_key=? ";
            args.add(productKey);
        }

        if (online != null && online) {
            sql += "and a.state=? ";
            args.add("online");
        } else {
            sql += "and a.state=? ";
            args.add("offline");
        }


        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword.trim() + "%";
            sql += "and (a.device_id like ? or a.device_name like ?) ";
            args.add(keyword);
            args.add(keyword);//两个参数
        }

        sql += String.format("order by create_at desc limit %d,%d", (page - 1) * size, size);

        List<DeviceInfo> list = jdbcTemplate.query(sql, (rs, rowNum) -> DeviceInfo.builder()
                .id(rs.getString("id"))
                .deviceId(rs.getString("device_id"))
                .deviceName(rs.getString("device_name"))
                .productKey(rs.getString("product_key"))
                .model(rs.getString("model"))
                .secret(rs.getString("secret"))
                .parentId(rs.getString("parent_id"))
                .locate(new DeviceInfo.Locate(rs.getString("longitude"), rs.getString("latitude")))
                .uid(rs.getString("uid"))
                .state(new DeviceInfo.State(
                        "online".equals(rs.getString("state")),
                        rs.getLong("online_time"),
                        rs.getLong("offline_time")
                ))
                .createAt(rs.getLong("create_at"))
                .build(), args.toArray());

        sql = sql.replaceAll("SELECT[\\s\\S]+FROM", "SELECT count(*) FROM ");
        sql = sql.replaceAll("order by create_at desc limit.*", "");
        Long total = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());

        //把当前页的deviceId串连起来作为in的参数
        String deviceIds = list.stream().map(d -> "'" + d.getDeviceId() + "'").collect(Collectors.joining(","));

        //取设备所属分组
        List<DeviceIdGroup> groups = list.isEmpty() ? new ArrayList<>() :
                jdbcTemplate.query("SELECT \n" +
                        "a.id,\n" +
                        "a.name, \n" +
                        "b.device_id as deviceId \n" +
                        "FROM\n" +
                        "device_group a \n" +
                        "JOIN device_group_mapping b on a.id=b.group_id\n" +
                        String.format("WHERE b.device_id in(%s)", deviceIds), new BeanPropertyRowMapper<>(DeviceIdGroup.class));

        //取设备标签
//        List<TbDeviceTag> tags = list.size() == 0 ? new ArrayList<>() :
//                jdbcTemplate.query("\n" +
//                        "SELECT\n" +
//                        "a.id,\n" +
//                        "a.code,\n" +
//                        "a.name,\n" +
//                        "a.value\n" +
//                        "FROM device_tag a " +
//                        String.format("WHERE a.device_id IN(%s)", deviceIds), new BeanPropertyRowMapper<>(TbDeviceTag.class));

        for (DeviceInfo device : list) {
            //设置设备分组
            Map<String, DeviceInfo.Group> groupMap = new HashMap<>();
            groups.stream().filter(g -> device.getDeviceId().equals(g.getDeviceId()))
                    .forEach(g -> groupMap.put(g.getId(),
                            new DeviceInfo.Group(g.getId(), g.getName())));
            device.setGroup(groupMap);

            //设置设备标签
//            Map<String, DeviceInfo.Tag> tagMap = new HashMap<>();
//            tags.stream().filter(t -> device.getDeviceId().equals(t.getDeviceId()))
//                    .forEach(t -> tagMap.put(t.getCode(),
//                            new DeviceInfo.Tag(t.getCode(), t.getName(), t.getValue())));
//            device.setTag(tagMap);
        }

        return new Paging<>(total, list);
    }


    @Override
    public void updateTag(String deviceId, DeviceInfo.Tag tag) {
        TbDeviceTag deviceTag = deviceTagService.findOneByConditions(new LambdaQueryWrapper<TbDeviceTag>()
                .eq(TbDeviceTag::getDeviceId, deviceId).eq(TbDeviceTag::getCode, tag.getId()));
        if (deviceTag != null) {
            deviceTag.setName(tag.getName());
            deviceTag.setValue(tag.getValue());
            deviceTagService.saveOrUpdate(deviceTag);
        } else {
            deviceTagService.saveOrUpdate(
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
        String sql = "SELECT COUNT(*) as value,product_key as name from " +
                "device_info %s GROUP BY product_key";
        List<Object> args = new ArrayList<>();
        if (StringUtils.isNotBlank(uid)) {
            sql = String.format(sql, "where uid=:uid");
            args.add(uid);
        } else {
            sql = String.format(sql, "");
        }
        List<DataItem> stats = new ArrayList<>();

        List<DataItem> rst = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(DataItem.class),
                args.toArray());
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
        return deviceGroupMappingService.count(new LambdaQueryWrapper<TbDeviceGroupMapping>()
                .eq(TbDeviceGroupMapping::getGroupId, groupId));
    }

    @Override
    @Transactional
    public void addToGroup(String deviceId, DeviceInfo.Group group) {
        String groupId = UUID.randomUUID().toString();
        deviceGroupMappingService.saveOrUpdate(new TbDeviceGroupMapping(groupId, deviceId, group.getId()));

        //更新设备数量
        updateGroupDeviceCount(groupId);
    }

    private void updateGroupDeviceCount(String groupId) {
        //更新设备数量
        TbDeviceGroup deviceGroup = deviceGroupService.getById(groupId);
        if (deviceGroup != null) {
            deviceGroup.setDeviceQty((int) countByGroupId(groupId));
            deviceGroupService.saveOrUpdate(deviceGroup);
        }
    }

    @Override
    public void updateGroup(String groupId, DeviceInfo.Group group) {
        //更新设备信息中的分组信息，关系数据库中不需要实现
    }

    @Override
    @Transactional
    public void removeGroup(String deviceId, String groupId) {
        jdbcTemplate.update("delete from device_group_mapping where device_id=? and group_id=?", deviceId, groupId);
        //更新设备数量
        updateGroupDeviceCount(groupId);
    }

    @Override
    @Transactional
    public void removeGroup(String groupId) {
        jdbcTemplate.update("delete from device_group_mapping where group_id=?", groupId);
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
        return MapstructUtils.convert(deviceInfoService.getById(s), DeviceInfo.class);
    }

    @Override
    public List<DeviceInfo> findByIds(Collection<String> ids) {
        return MapstructUtils.convert(deviceInfoService.listByIds(ids), DeviceInfo.class);
    }

    @Override
    @Transactional
    public DeviceInfo save(DeviceInfo data) {
        TbDeviceInfo vo = deviceInfoService.findOneByConditions(new LambdaQueryWrapper<TbDeviceInfo>()
                .eq(TbDeviceInfo::getDeviceId, data.getDeviceId()));
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
        deviceInfoService.saveOrUpdate(vo);

        //设备分组转换
        Map<String, DeviceInfo.Group> groupMap = data.getGroup();
        groupMap.forEach((id, group) -> {
            TbDeviceGroupMapping mapping = deviceGroupMappingService.findOneByConditions(new LambdaQueryWrapper<TbDeviceGroupMapping>()
                    .eq(TbDeviceGroupMapping::getDeviceId, data.getDeviceId()).eq(TbDeviceGroupMapping::getGroupId, id));
            if (mapping == null) {
                //保存设备分组与设备对应关系
                deviceGroupMappingService.saveOrUpdate(new TbDeviceGroupMapping(
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
        deviceInfoService.removeById(s);
    }

    @Override
    public void deleteByIds(Collection<String> ids) {
        deviceInfoService.removeBatchByIds(ids);
    }


    @Override
    public long count() {
        return deviceInfoService.count();
    }

    @Override
    public Paging<DeviceInfo> findAll(PageRequest<DeviceInfo> pageRequest) {
        Page<TbDeviceInfo> rowPage = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<TbDeviceInfo> page = deviceInfoService.page(rowPage, new LambdaQueryWrapper<>());
        return new Paging<>(page.getTotal(), MapstructUtils.convert(page.getRecords(), DeviceInfo.class));
    }

    @Override
    public DeviceInfo findOneByCondition(DeviceInfo data) {
        return null;
    }

    @Override
    public List<DeviceInfo> findAllByCondition(DeviceInfo data) {
        LambdaQueryWrapper<TbDeviceInfo> wrapper = new LambdaQueryWrapper<TbDeviceInfo>();
        wrapper.eq(TbDeviceInfo::getId, data.getId());
        wrapper.eq(TbDeviceInfo::getState, data.getState().isOnline() ? "online" : "offline");

        List<TbDeviceInfo> result = deviceInfoService.findByConditions(wrapper);

        return MapstructUtils.convert(result, DeviceInfo.class);
    }

}
