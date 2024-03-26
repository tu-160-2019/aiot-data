package srt.cloud.framework.dbswitch.pgwriter.util;

import srt.cloud.framework.dbswitch.pgwriter.exceptions.PgConnectionException;
import org.postgresql.PGConnection;

import java.sql.Connection;
import java.util.Optional;

public final class PostgreSqlUtils {

  private PostgreSqlUtils() {
  }

  public static PGConnection getPGConnection(final Connection connection) {
    return tryGetPGConnection(connection)
        .orElseThrow(() -> new PgConnectionException("Could not obtain a PGConnection"));
  }

  public static Optional<PGConnection> tryGetPGConnection(final Connection connection) {
    final Optional<PGConnection> fromCast = tryCastConnection(connection);
    if (fromCast.isPresent()) {
      return fromCast;
    }
    return tryUnwrapConnection(connection);
  }

  private static Optional<PGConnection> tryCastConnection(final Connection connection) {
    if (connection instanceof PGConnection) {
      return Optional.of((PGConnection) connection);
    }
    return Optional.empty();
  }

  private static Optional<PGConnection> tryUnwrapConnection(final Connection connection) {
    try {
      if (connection.isWrapperFor(PGConnection.class)) {
        return Optional.of(connection.unwrap(PGConnection.class));
      }
    } catch (Exception e) {
      // do nothing
    }
    return Optional.empty();
  }

  public static final char QuoteChar = '"';

  public static String quoteIdentifier(String identifier) {
    return requiresQuoting(identifier) ? (QuoteChar + identifier + QuoteChar) : identifier;
  }

  public static String getFullyQualifiedTableName(String schemaName, String tableName,
      boolean usePostgresQuoting) {
    if (usePostgresQuoting) {
      return StringUtils.isNullOrWhiteSpace(schemaName) ? quoteIdentifier(tableName)
          : String.format("%s.%s", quoteIdentifier(schemaName), quoteIdentifier(tableName));
    }

    if (StringUtils.isNullOrWhiteSpace(schemaName)) {
      return tableName;
    }

    return String.format("%1$s.%2$s", schemaName, tableName);
  }

  private static boolean requiresQuoting(String identifier) {

    char first = identifier.charAt(0);
    char last = identifier.charAt(identifier.length() - 1);

    if (first == QuoteChar && last == QuoteChar) {
      return false;
    }

    return true;
  }
}
