package srt.cloud.framework.dbswitch.dbcommon.database.impl;

import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.common.util.HivePrepareUtils;
import srt.cloud.framework.dbswitch.dbcommon.constant.Constants;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

@Slf4j
public class HiveDatabaseOperator extends MysqlDatabaseOperator {

  public HiveDatabaseOperator(DataSource dataSource) {
    super(dataSource);
  }


  @Override
  public StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
										   List<String> orders) {
    String sql = String.format("select `%s` from `%s`.`%s` order by `%s` asc  ",
        StringUtils.join(fields, "`,`"),
        schemaName, tableName, StringUtils.join(orders, "`,`"));
    return selectHiveTableData(sql, schemaName, tableName);
  }

  @Override
  public StatementResultSet queryTableData(String schemaName, String tableName,
										   List<String> fields) {
    String sql = String.format("select `%s` from `%s`.`%s` ",
        StringUtils.join(fields, "`,`"), schemaName, tableName);
    return selectHiveTableData(sql, schemaName, tableName);
  }

  private StatementResultSet selectHiveTableData(String sql, String schemaName, String tableName) {
    if (log.isDebugEnabled()) {
      log.debug("Query table data sql :{}", sql);
    }

    try {
      Connection connection = dataSource.getConnection();
      HivePrepareUtils.prepare(connection, schemaName, tableName);

      StatementResultSet srs = new StatementResultSet();
      srs.setConnection(connection);
      srs.setAutoCommit(srs.getConnection().getAutoCommit());
      srs.getConnection().setAutoCommit(false);
      srs.setStatement(srs.getConnection()
          .createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
      srs.getStatement().setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
      srs.getStatement().setFetchSize(this.fetchSize);
      srs.setResultset(srs.getStatement().executeQuery(sql));
      return srs;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

}
