/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.model.product.ThingModel;
import cc.iotkit.temporal.IDbStructureData;
import cc.iotkit.temporal.ts.config.Constants;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.dm.DbField;
import cc.iotkit.temporal.ts.dm.FieldParser;
import cc.iotkit.temporal.ts.dm.TableManager;
import cc.iotkit.temporal.ts.dm.TsField;
import lombok.extern.slf4j.Slf4j;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DbStructureDataImpl implements IDbStructureData {

    @Autowired
    private TsTemplate tsTemplate;

    /**
     * 根据物模型创建超级表
     */
    @Override
    public void defineThingModel(ThingModel thingModel) {
        //获取物模型中的属性定义
        List<TsField> fields = FieldParser.parse(thingModel);
        String tbName = Constants.getProductPropertySTableName(thingModel.getProductKey());
        String sql = TableManager.getCreateSTableSql(tbName,
                fields);
        if (sql == null) {
            return;
        }
        System.out.println(sql);
        tsTemplate.execute(sql);

        createHypertable(tbName, "device_id");

    }

    private void createHypertable(String tbName, String partitionCol) {
        String createSTableIndexSql = TableManager.getCreateSTableIndexSql(tbName, partitionCol);
        try {
            System.out.println(createSTableIndexSql);

            tsTemplate.execute(createSTableIndexSql);
        } catch (Exception e) {
            log.info("createHypertable error:{}", e.getMessage());
        }

    }

    /**
     * 根据物模型更新超级表结构
     */
    @Override
    public void updateThingModel(ThingModel thingModel) {
        //获取旧字段信息
        String tbName = Constants.getProductPropertySTableName(thingModel.getProductKey());
        String sql = TableManager.getDescTableSql(tbName);
        if (sql == null) {
            return;
        }

        tsTemplate.execute(sql);

        List<DbField> fieldsInDb = tsTemplate.query(sql, new BeanPropertyRowMapper<DbField>(DbField.class));

        List<TsField> newFields = FieldParser.parse(thingModel);
        List<TsField> oldFields = FieldParser.parse(fieldsInDb);

        //对比差异

        //找出修改的字段
        List<TsField> modifyFields = newFields.stream().filter((f) -> oldFields.stream()
                        .anyMatch(old ->
                                old.getName().equals(f.getName()) //字段名相同
                                        //字段类型或长度不同
                                        && (!old.getType().equals(f.getType()) || old.getLength() != f.getLength())
                        ))
                .collect(Collectors.toList());
        if (modifyFields.size() > 0) {
            sql = TableManager.getModifySTableColumnSql(tbName, modifyFields);
            log.info("modify column:{}", sql);

            tsTemplate.execute(sql);

        }

        //找出新增的字段
        List<TsField> addFields = newFields.stream().filter((f) -> oldFields.stream()
                        .noneMatch(old -> old.getName().equals(f.getName())))
                .collect(Collectors.toList());
        if (addFields.size() > 0) {
            sql = TableManager.getAddSTableColumnSql(tbName, addFields);
            log.info("add column:{}", sql);

            tsTemplate.execute(sql);
        }


        //找出删除的字段
        List<TsField> dropFields = oldFields.stream().filter((f) ->
                        !"time".equals(f.getName()) &&
                                !"device_id".equals(f.getName()) && newFields.stream()
                                //字段名不是time且没有相同字段名的
                                .noneMatch(n -> n.getName().equals(f.getName())))
                .collect(Collectors.toList());
        if (dropFields.size() > 0) {

            sql = TableManager.getDropSTableColumnSql(tbName, dropFields);
            log.info("drop column:{}", sql);
            tsTemplate.execute(sql);

        }
    }

    /**
     * 初始化其它数据结构
     */
    @Override
    @PostConstruct
    public void initDbStructure() {
        //创建规则日志表
        DSLContext dslBuilder = DSL.using(SQLDialect.POSTGRES);

        CreateTableColumnStep ruleLogStep = dslBuilder.createTableIfNotExists("rule_log")
                .column("time", SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false))
                .column("state1", SQLDataType.VARCHAR(50))
                .column("content", SQLDataType.VARCHAR(1024))
                .column("success", SQLDataType.BOOLEAN)
                .column("rule_id", SQLDataType.VARCHAR(50).nullable(false));
        String sql = ruleLogStep.getSQL();
        System.out.println(sql);
        tsTemplate.execute(sql);
        // 按时间和rule_id分区
        createHypertable("rule_log", "rule_id");


        CreateTableColumnStep taskLogStep = dslBuilder.createTableIfNotExists("task_log")
                .column("time", SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false))
                .column("content", SQLDataType.VARCHAR(1024))
                .column("success", SQLDataType.BOOLEAN)
                .column("task_id", SQLDataType.VARCHAR(50).nullable(false));
        String taskLogsql = taskLogStep.getSQL();

        System.out.println(taskLogsql);
        tsTemplate.execute(taskLogsql);
        // 按时间和task_id分区
        createHypertable("task_log", "task_id");

        CreateTableColumnStep thingModelStep = dslBuilder.createTableIfNotExists("thing_model_message")
                .column("time", SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false))
                .column("mid", SQLDataType.NVARCHAR(50))
                .column("product_key", SQLDataType.NVARCHAR(50))
                .column("device_name", SQLDataType.NVARCHAR(50))
                .column("uid", SQLDataType.NVARCHAR(50))
                .column("type", SQLDataType.NVARCHAR(20))
                .column("identifier", SQLDataType.NVARCHAR(50))
                .column("code", SQLDataType.INTEGER)
                .column("data", SQLDataType.NVARCHAR(1024))
                .column("report_time", SQLDataType.NVARCHAR(1024))
                .column("device_id", SQLDataType.NVARCHAR(50));

        String thingModelsql = thingModelStep.getSQL();

        System.out.println(thingModelsql);
        tsTemplate.execute(thingModelsql);
        createHypertable("thing_model_message", "device_id");

        //创建虚拟设备日志表
        CreateTableColumnStep virtualStep = dslBuilder.createTableIfNotExists("virtual_device_log")
                .column("time", SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false))
                .column("virtual_device_name", SQLDataType.NVARCHAR(50))
                .column("device_total", SQLDataType.INTEGER)
                .column("result", SQLDataType.NVARCHAR(1024))
                .column("virtual_device_id", SQLDataType.NVARCHAR(50));

        String virtualsql = virtualStep.getSQL();
        System.out.println(virtualsql);

        tsTemplate.execute(virtualsql);
        createHypertable("virtual_device_log", "virtual_device_id");

    }
}
