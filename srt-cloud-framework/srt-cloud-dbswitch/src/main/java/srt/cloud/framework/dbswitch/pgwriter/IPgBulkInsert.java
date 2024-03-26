package srt.cloud.framework.dbswitch.pgwriter;

import org.postgresql.PGConnection;

import java.sql.SQLException;
import java.util.stream.Stream;

public interface IPgBulkInsert<TEntity> {

  void saveAll(PGConnection connection, Stream<TEntity> entities) throws SQLException;
}
