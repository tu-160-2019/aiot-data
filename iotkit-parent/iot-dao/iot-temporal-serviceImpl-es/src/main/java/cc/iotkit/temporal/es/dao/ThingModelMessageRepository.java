/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.dao;

import cc.iotkit.temporal.es.document.DocThingModelMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ThingModelMessageRepository extends ElasticsearchRepository<DocThingModelMessage, String> {
}
