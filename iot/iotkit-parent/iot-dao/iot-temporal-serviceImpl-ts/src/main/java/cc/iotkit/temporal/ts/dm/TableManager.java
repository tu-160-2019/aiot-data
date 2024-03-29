/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.dm;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.tools.StringUtils;

import java.util.List;

import static org.jooq.impl.DSL.*;

public class TableManager {

   private static final DSLContext sqlBuilder = DSL.using(SQLDialect.POSTGRES);

    public static DSLContext getSqlBuilder() {
        return sqlBuilder;
    }

    /**
     * 获取创建表sql
     */
    public static String getCreateSTableSql(String tbName, List<TsField> fields) {
        if (fields.size() == 0) {
            return null;
        }

        CreateTableColumnStep tableColumnStep = sqlBuilder.createTable(tbName)
                .column("time", SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false))
                .column(field("device_id", SQLDataType.NCHAR.length(50).nullable(false)));

        //生成字段片段

        for (TsField field : fields) {
            tableColumnStep.column(FieldParser.getFieldDefine(field));

        }


        return tableColumnStep.getSQL(ParamType.INLINED);

    }

    public static String getCreateSTableIndexSql(String tbName, String partitionCol) {
        //根据时间和设备纬度分区
        String sql = null;
        if(StringUtils.isBlank(partitionCol)){
            //  只根据时间分区
            sql=  String.format(" SELECT create_hypertable('%s', 'time') ;", tbName);
        }else{
            sql=  String.format(" SELECT * FROM create_hypertable('%s', 'time'," +
                    "  partitioning_column => '%s'," +
                    "  number_partitions => 4" +
                    ") ;", tbName, partitionCol );

        }

        return sql;

    }

    public static String getCreateTableIndexSql(String tbName) {

        CreateIndexIncludeStep step = sqlBuilder.createIndexIfNotExists(tbName + "_index").on(
                table(name(tbName)),
                field(name("device_id")),
                field(name("time")).desc());

        return step.getSQL(ParamType.INLINED);

    }

    /**
     * 取正确的表名
     *
     * @param name 表象
     */
    public static String rightTbName(String name) {
        return name.toLowerCase().replace("-", "_");
    }

    /**
     * 获取表详情的sql
     */
    public static String getDescTableSql(String tbName) {


      String sql =String.format( " select a.attname as name," +
              " t.typname as type, " +
              "a.attlen as length," +
              " case when a.attnotnull='t' then '1' else '0' end as nullable," +
              " case when b.pk='t' then '1' else '0' end as isPk " +
              "from pg_class e, pg_attribute a left join pg_type t on a.atttypid = t.oid " +
              "left join (select pg_constraint.conname,pg_constraint.contype,pg_attribute.attname as pk " +
              "from pg_constraint " +
              " inner join pg_class on pg_constraint.conrelid = pg_class.oid" +
              " inner join pg_attribute on pg_attribute.attrelid = pg_class.oid " +
              " and  pg_attribute.attnum = any(pg_constraint.conkey) where contype='p')" +
              " b on a.attname=b.pk where e.relname = '%s'" +
              " and a.attnum > 0 and a.attrelid = e.oid and t.typname is not null ;",tbName);

    return sql;



    }

    /**
     * 获取添加字段sql
     */
    public static String getAddSTableColumnSql(String tbName, List<TsField> fields) {

        AlterTableStep alterTableStep = sqlBuilder.alterTable(tbName);

        AlterTableFinalStep addStep = null;
        for (TsField o : fields) {
            addStep = alterTableStep.add(FieldParser.getFieldDefine(o));
        }
        return addStep.getSQL();
    }

    /**
     * 获取修改字段sql
     */
    public static String getModifySTableColumnSql(String tbName, List<TsField> fields) {
        AlterTableStep alterTableStep = sqlBuilder.alterTable(tbName);
        AlterTableFinalStep step = null;
        for (TsField o : fields) {
            Field fieldDefine = FieldParser.getFieldDefine(o);
            step = alterTableStep.alterColumn(o.getName()).set(fieldDefine.getDataType());
        }
        return step.getSQL();
    }

    /**
     * 获取删除字段sql
     */
    public static String getDropSTableColumnSql(String tbName, List<TsField> fields) {

        AlterTableStep alterTableStep = sqlBuilder.alterTable(tbName);


        AlterTableFinalStep step = null;
        for (TsField o : fields) {
            step = alterTableStep.dropColumnIfExists(FieldParser.getFieldDefine(o));
        }

        return step.getSQL();
    }

}
