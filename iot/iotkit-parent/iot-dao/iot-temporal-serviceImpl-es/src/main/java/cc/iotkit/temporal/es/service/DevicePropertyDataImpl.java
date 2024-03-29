/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.temporal.IDevicePropertyData;
import cc.iotkit.temporal.es.document.DocDeviceProperty;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DevicePropertyDataImpl implements IDevicePropertyData {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    private final Set<String> indexSet = new HashSet<>();

    @Override
    public List<DeviceProperty> findDevicePropertyHistory(String deviceId, String name, long start, long end, int size) {
        String index = getIndex(deviceId, name);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("deviceId", deviceId))
                                .must(QueryBuilders.rangeQuery("time")
                                        .from(start, true).to(end, true))
                )
                .withSorts(new FieldSortBuilder("time").order(SortOrder.ASC))
                .build();
        SearchHits<DocDeviceProperty> result = template.search(query, DocDeviceProperty.class, IndexCoordinates.of(index));
        return result.getSearchHits().stream()
                .map(h -> MapstructUtils.convert(h.getContent(), DeviceProperty.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addProperties(String deviceId, Map<String, DevicePropertyCache> properties, long time) {
        properties.forEach((key, val) -> {
            DevicePropertyCache propertyCache = (DevicePropertyCache) val;
            String index = getIndex(deviceId, key);
            long occurred = Objects.nonNull(propertyCache.getOccurred()) ? propertyCache.getOccurred() : time;
            template.save(
                    new DocDeviceProperty(UUID.randomUUID().toString(), deviceId, key, propertyCache.getValue(), occurred),
                    IndexCoordinates.of(index)
            );
        });
    }

    private String getIndex(String deviceId, String name) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        if (deviceInfo == null) {
            return null;
        }
        String pk = deviceInfo.getProductKey();
        String index = String.format("device_property_%s_%s", pk, name).toLowerCase();
        if (!indexSet.contains(index)) {
            IndexCoordinates indexCoordinates = IndexCoordinates.of(index);
            if (!template.indexOps(indexCoordinates).exists()) {
                // 根据索引实体，获取mapping字段
                Document mapping = template.indexOps(indexCoordinates).createMapping(DocDeviceProperty.class);
                template.indexOps(indexCoordinates).create();
                // 创建索引mapping
                template.indexOps(indexCoordinates).putMapping(mapping);
            }
            indexSet.add(index);
        }
        return index;
    }

}
