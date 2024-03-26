package srt.cloud.framework.dbswitch.core.util;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.DatabaseFactory;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;

import java.sql.Connection;
import java.util.List;

public final class PostgresUtils {

  public static String getTableDDL(Connection connection, String schema, String table) {
    AbstractDatabase db = DatabaseFactory.getDatabaseInstance(ProductTypeEnum.POSTGRESQL);
    List<ColumnDescription> columnDescriptions = db.queryTableColumnMeta(connection, schema, table);
    List<String> pks = db.queryTablePrimaryKeys(connection, schema, table);
    return GenerateSqlUtils.getDDLCreateTableSQL(
        db.getDatabaseType(), columnDescriptions, pks, schema, table, false);
  }

  private PostgresUtils() {
	  throw new IllegalStateException();
  }
}
