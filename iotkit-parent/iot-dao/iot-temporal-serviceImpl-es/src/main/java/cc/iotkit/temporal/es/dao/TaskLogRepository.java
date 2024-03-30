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

import cc.iotkit.temporal.es.document.DocTaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TaskLogRepository extends ElasticsearchRepository<DocTaskLog, String> {

    void deleteByTaskId(String taskId);

    Page<DocTaskLog> findByTaskIdOrderByLogAtDesc(String taskId, Pageable pageable);

}
