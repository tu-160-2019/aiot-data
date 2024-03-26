package srt.cloud.framework.dbswitch.pgwriter.exceptions;

public class ValueHandlerAlreadyRegisteredException extends RuntimeException {

  public ValueHandlerAlreadyRegisteredException(String message) {
    super(message);
  }

  public ValueHandlerAlreadyRegisteredException() {
  }

  public ValueHandlerAlreadyRegisteredException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValueHandlerAlreadyRegisteredException(Throwable cause) {
    super(cause);
  }

  public ValueHandlerAlreadyRegisteredException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
