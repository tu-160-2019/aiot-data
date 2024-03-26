// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.ddl.sql;

import srt.cloud.framework.dbswitch.sql.constant.Const;
import srt.cloud.framework.dbswitch.sql.ddl.AbstractDatabaseDialect;
import srt.cloud.framework.dbswitch.sql.ddl.AbstractSqlDdlOperator;
import srt.cloud.framework.dbswitch.sql.ddl.pojo.ColumnDefinition;
import srt.cloud.framework.dbswitch.sql.ddl.pojo.TableDefinition;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.GreenplumDialectImpl;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.PostgresDialectImpl;

import java.util.Objects;

/**
 * Alter语句操作类
 *
 * @author jrl
 */
public class DdlSqlAlterTable extends AbstractSqlDdlOperator {

  protected enum AlterTypeEnum {
    /**
     * 添加字段操作
     */
    ADD(1),

    /**
     * 删除字段操作
     */
    DROP(2),

    /**
     * 修改字段操作
     */
    MODIFY(3),

    /**
     * 重命名操作
     */
    RENAME(4);

    private int index;

    AlterTypeEnum(int idx) {
      this.index = idx;
    }

    public int getIndex() {
      return index;
    }
  }

  private TableDefinition table;
  private AlterTypeEnum alterType;

  public DdlSqlAlterTable(TableDefinition t, String handle) {
    super(Const.ALTER_TABLE);
    this.table = t;
    alterType = AlterTypeEnum.valueOf(handle.toUpperCase());
  }

  @Override
  public String toSqlString(AbstractDatabaseDialect dialect) {
    String fullTableName = dialect.getSchemaTableName(table.getSchemaName(), table.getTableName());

    StringBuilder sb = new StringBuilder();
    sb.append(this.getName());
    sb.append(fullTableName);

    if (table.getColumns().size() < 1) {
      throw new RuntimeException("Alter table need one column at least!");
    }

    if (AlterTypeEnum.ADD == alterType) {
      if (dialect instanceof PostgresDialectImpl || dialect instanceof GreenplumDialectImpl) {
        //PostgreSQL/Greenplum数据库的add只支持一列，不支持多列
        if (table.getColumns().size() != 1) {
          throw new RuntimeException(
              "Alter table for PostgreSQL/Greenplum only can add one column!");
        }

        sb.append(" ADD ");
        ColumnDefinition cd = table.getColumns().get(0);
        sb.append(dialect.getFieldDefination(cd));
      } else {
        sb.append(" ADD (");
        for (int i = 0; i < table.getColumns().size(); ++i) {
          ColumnDefinition cd = table.getColumns().get(i);
          sb.append((i > 0) ? "," : " ");
          sb.append(dialect.getFieldDefination(cd));
        }
        sb.append(")");
      }
    } else if (AlterTypeEnum.DROP == alterType) {
      if (table.getColumns().size() != 1) {
        throw new RuntimeException("Alter table only can drop one column!");
      }

      ColumnDefinition cd = table.getColumns().get(0);
      sb.append(" DROP ");
      sb.append(dialect.getQuoteFieldName(cd.getColumnName()));
    } else if (AlterTypeEnum.MODIFY == alterType) {
      if (table.getColumns().size() != 1) {
        throw new RuntimeException("Alter table only can modify one column!");
      }

      ColumnDefinition cd = table.getColumns().get(0);
      if (dialect instanceof PostgresDialectImpl || dialect instanceof GreenplumDialectImpl) {
        //PostgreSQL/Greenplum数据库的modify需要单独拆分
        String typename = dialect.getFieldTypeName(cd);
        boolean nullable = cd.isNullable();
        String defaultValue = cd.getDefaultValue();
        sb.append(
            " ALTER COLUMN " + dialect.getQuoteFieldName(cd.getColumnName()) + " TYPE " + typename);
        if (nullable) {
          sb.append(",ALTER COLUMN " + dialect.getQuoteFieldName(cd.getColumnName())
              + " SET DEFAULT NULL");
        } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty() && !"NULL"
            .equalsIgnoreCase(defaultValue)) {
          sb.append(
              ",ALTER COLUMN " + dialect.getQuoteFieldName(cd.getColumnName()) + " SET DEFAULT '"
                  + defaultValue + "'");
        } else {
          sb.append(
              ",ALTER COLUMN " + dialect.getQuoteFieldName(cd.getColumnName()) + " SET NOT NULL");
        }
      } else {
        sb.append(" MODIFY  ");
        sb.append(dialect.getFieldDefination(cd));
      }
    } else {
      // 当前不支持rename及其他操作
      throw new RuntimeException("Alter table unsupported operation : " + alterType.name());
    }

    return sb.toString();
  }

}
