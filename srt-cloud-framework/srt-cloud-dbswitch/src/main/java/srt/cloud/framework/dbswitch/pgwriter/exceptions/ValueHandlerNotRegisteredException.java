package srt.cloud.framework.dbswitch.pgwriter.exceptions;

public class ValueHandlerNotRegisteredException extends RuntimeException {

  public ValueHandlerNotRegisteredException(String message) {
    super(message);
  }

  public ValueHandlerNotRegisteredException() {
  }

  public ValueHandlerNotRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValueHandlerNotRegisteredException(Throwable cause) {
    super(cause);
  }

  public ValueHandlerNotRegisteredException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
