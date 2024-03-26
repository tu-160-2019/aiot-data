package srt.cloud.framework.dbswitch.pgwriter.exceptions;

public class PgConnectionException extends RuntimeException {

  public PgConnectionException(String message) {
    super(message);
  }

  public PgConnectionException() {
  }

  public PgConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public PgConnectionException(Throwable cause) {
    super(cause);
  }

  public PgConnectionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
