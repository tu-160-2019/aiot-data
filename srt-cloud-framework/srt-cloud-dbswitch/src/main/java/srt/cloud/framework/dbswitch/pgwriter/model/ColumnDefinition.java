package srt.cloud.framework.dbswitch.pgwriter.model;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.PgBinaryWriter;

import java.util.function.BiConsumer;

public class ColumnDefinition<TEntity> {

  private final String columnName;

  private final BiConsumer<PgBinaryWriter, TEntity> write;

  public ColumnDefinition(String columnName, BiConsumer<PgBinaryWriter, TEntity> write) {
    this.columnName = columnName;
    this.write = write;
  }

  public String getColumnName() {
    return columnName;
  }

  public BiConsumer<PgBinaryWriter, TEntity> getWrite() {
    return write;
  }

  @Override
  public String toString() {
    return String
        .format("ColumnDefinition (ColumnName = {%1$s}, Serialize = {%2$s})", columnName, write);
  }
}
