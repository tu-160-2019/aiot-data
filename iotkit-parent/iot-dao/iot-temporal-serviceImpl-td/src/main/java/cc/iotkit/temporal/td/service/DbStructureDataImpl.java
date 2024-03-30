/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.service;

import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.model.product.ThingModel;
import cc.iotkit.temporal.IDbStructureData;
import cc.iotkit.temporal.td.config.Constants;
import cc.iotkit.temporal.td.dm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DbStructureDataImpl implements IDbStructureData {

    @Autowired
    private TdRestApi tdRestApi;

    /**
     * 根据物模型创建超级表
     */
    @Override
    public void defineThingModel(ThingModel thingModel) {
        //获取物模型中的属性定义
        List<TdField> fields = FieldParser.parse(thingModel);
        String tbName = Constants.getProductPropertySTableName(thingModel.getProductKey());
        //生成sql
        String sql = TableManager.getCreateSTableSql(tbName,
                fields,
                new TdField("device_id", "NCHAR", 50));
        if (sql == null) {
            return;
        }
        log.info("executing sql:{}", sql);

        //执行sql
        TdResponse response = tdRestApi.execSql(sql);
        if (TdResponse.CODE_SUCCESS != response.getCode()) {
            throw new BizException(String.format(
                    "create td stable failed,code:%s,desc:%s"
                    , response.getCode(), response.getDesc()));
        }
    }

    /**
     * 根据物模型更新超级表结构
     */
    @Override
    public void updateThingModel(ThingModel thingModel) {
        try {
            //获取旧字段信息
            String tbName = Constants.getProductPropertySTableName(thingModel.getProductKey());
            String sql = TableManager.getDescTableSql(tbName);
            TdResponse response = tdRestApi.execSql(sql);
            int code = response.getCode();
            if (code != TdResponse.CODE_SUCCESS) {
                if (TdResponse.CODE_TB_NOT_EXIST == code) {
                    defineThingModel(thingModel);
                    return;
                }

                throw new BizException("get des table error:" + JsonUtils.toJsonString(response));
            }

            List<TdField> oldFields = FieldParser.parse(response.getData());
            List<TdField> newFields = FieldParser.parse(thingModel);
            //对比差异

            //找出新增的字段
            List<TdField> addFields = newFields.stream().filter((f) -> oldFields.stream()
                            .noneMatch(old -> old.getName().equals(f.getName())))
                    .collect(Collectors.toList());
            if (!addFields.isEmpty()) {
                sql = TableManager.getAddSTableColumnSql(tbName, addFields);
                response = tdRestApi.execSql(sql);
                if (response.getCode() != TdResponse.CODE_SUCCESS) {
                    throw new BizException("add table column error:" + JsonUtils.toJsonString(response));
                }
            }

            //找出修改的字段
            List<TdField> modifyFields = newFields.stream().filter((f) -> oldFields.stream()
                            .anyMatch(old ->
                                    old.getName().equals(f.getName()) //字段名相同
                                            //字段类型或长度不同
                                            && (!old.getType().equals(f.getType()) || old.getLength() != f.getLength())
                            ))
                    .collect(Collectors.toList());

            if (!modifyFields.isEmpty()) {
                sql = TableManager.getModifySTableColumnSql(tbName, modifyFields);
                response = tdRestApi.execSql(sql);
                if (response.getCode() != TdResponse.CODE_SUCCESS) {
                    throw new BizException("modify table column error:" + JsonUtils.toJsonString(response));
                }
            }

            //找出删除的字段
            List<TdField> dropFields = oldFields.stream().filter((f) ->
                            !"time".equals(f.getName()) &&
                                    !"device_id".equals(f.getName()) && newFields.stream()
                                    //字段名不是time且没有相同字段名的
                                    .noneMatch(n -> n.getName().equals(f.getName())))
                    .collect(Collectors.toList());
            if (!dropFields.isEmpty()) {
                sql = TableManager.getDropSTableColumnSql(tbName, dropFields);
                response = tdRestApi.execSql(sql);
                if (response.getCode() != TdResponse.CODE_SUCCESS) {
                    throw new BizException("drop table column error:" + JsonUtils.toJsonString(response));
                }
            }
        } catch (Throwable e) {
            log.error("update thingmodel stable failed", e);
        }
    }

    /**
     * 初始化其它数据结构
     */
    @Override
    @PostConstruct
    public void initDbStructure() {
        tdRestApi.execSql("CREATE DATABASE IF NOT EXISTS iotkit KEEP 365 DURATION 10 BUFFER 16 WAL_LEVEL 1;");

        //创建规则日志超级表
        String sql = TableManager.getCreateSTableSql("rule_log", List.of(
                new TdField("state1", "NCHAR", 32),
                new TdField("content", "NCHAR", 1024),
                new TdField("success", "BOOL", -1)
        ), new TdField("rule_id", "NCHAR", 50));
        TdResponse response = tdRestApi.execSql(sql);
        if (response.getCode() != TdResponse.CODE_SUCCESS) {
            throw new BizException("create stable rule_log error:" + JsonUtils.toJsonString(response));
        }

        //创建规则日志超级表
        sql = TableManager.getCreateSTableSql("task_log", List.of(
                new TdField("content", "NCHAR", 1024),
                new TdField("success", "BOOL", -1)
        ), new TdField("task_id", "NCHAR", 50));
        response = tdRestApi.execSql(sql);
        if (response.getCode() != TdResponse.CODE_SUCCESS) {
            throw new BizException("create stable task_log error:" + JsonUtils.toJsonString(response));
        }

        //创建物模型消息超级表
        sql = TableManager.getCreateSTableSql("thing_model_message", List.of(
                new TdField("mid", "NCHAR", 50),
                new TdField("product_key", "NCHAR", 50),
                new TdField("device_name", "NCHAR", 50),
                new TdField("uid", "NCHAR", 50),
                new TdField("type", "NCHAR", 20),
                new TdField("identifier", "NCHAR", 50),
                new TdField("code", "INT", -1),
                new TdField("data", "NCHAR", 1024),
                new TdField("report_time", "BIGINT", -1)
        ), new TdField("device_id", "NCHAR", 50));
        response = tdRestApi.execSql(sql);
        if (response.getCode() != TdResponse.CODE_SUCCESS) {
            throw new BizException("create stable thing_model_message error:" + JsonUtils.toJsonString(response));
        }

        //创建虚拟设备日志超级表
        sql = TableManager.getCreateSTableSql("virtual_device_log", List.of(
                new TdField("virtual_device_name", "NCHAR", 50),
                new TdField("device_total", "INT", -1),
                new TdField("result", "NCHAR", 1024)
        ), new TdField("virtual_device_id", "NCHAR", 50));
        response = tdRestApi.execSql(sql);
        if (response.getCode() != TdResponse.CODE_SUCCESS) {
            throw new BizException("create stable virtual_device_log error:" + JsonUtils.toJsonString(response));
        }

    }
}
