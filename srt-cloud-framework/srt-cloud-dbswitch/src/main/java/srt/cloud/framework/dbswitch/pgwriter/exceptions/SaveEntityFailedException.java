package srt.cloud.framework.dbswitch.pgwriter.exceptions;

public class SaveEntityFailedException extends RuntimeException {

  public SaveEntityFailedException(String message) {
    super(message);
  }

  public SaveEntityFailedException() {
  }

  public SaveEntityFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public SaveEntityFailedException(Throwable cause) {
    super(cause);
  }

  public SaveEntityFailedException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
