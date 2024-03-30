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

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.model.stats.TimeData;
import cc.iotkit.temporal.IThingModelMessageData;
import cc.iotkit.temporal.es.dao.ThingModelMessageRepository;
import cc.iotkit.temporal.es.document.DocThingModelMessage;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThingModelMessageDataImpl implements IThingModelMessageData {

    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private ThingModelMessageRepository thingModelMessageRepository;

    @Override
    public Paging<ThingModelMessage> findByTypeAndIdentifier(String deviceId, String type,
                                                             String identifier,
                                                             int page, int size) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("deviceId", deviceId));
        if (StringUtils.isNotBlank(type)) {
            builder.must(QueryBuilders.termQuery("type", type));
        }
        if (StringUtils.isNotBlank(identifier)) {
            builder.must(QueryBuilders.matchPhraseQuery("identifier", identifier));
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(builder)
                .withPageable(PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("time"))))
                .build();
        SearchHits<DocThingModelMessage> result = template.search(query, DocThingModelMessage.class);
        return new Paging<>(result.getTotalHits(), result.getSearchHits().stream()
                .map(m -> MapstructUtils.convert(m.getContent(), ThingModelMessage.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<TimeData> getDeviceMessageStatsWithUid(String uid, long start, long end) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("time")
                        .from(start, true).to(end, true));
        if (uid != null) {
            queryBuilder =
                    queryBuilder.must(QueryBuilders.termQuery("uid", uid));
        }

        //按小时统计消息数量
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withAggregations(AggregationBuilders.dateHistogram("agg")
                        .field("time")
                        .calendarInterval(DateHistogramInterval.HOUR)
                        .calendarInterval(DateHistogramInterval.hours(1))
                )
                .build();

        ElasticsearchAggregations result = (ElasticsearchAggregations) template
                .search(query, DocThingModelMessage.class).getAggregations();
        ParsedDateHistogram histogram = result.aggregations().get("agg");

        List<TimeData> data = new ArrayList<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            long seconds = ((ZonedDateTime) bucket.getKey()).toInstant().getEpochSecond();
            data.add(new TimeData(seconds * 1000, bucket.getDocCount()));
        }

        return data;
    }

    @Override
    public void add(ThingModelMessage msg) {
        thingModelMessageRepository.save(MapstructUtils.convert(msg, DocThingModelMessage.class));
    }

    @Override
    public long count() {
        return thingModelMessageRepository.count();
    }
}
