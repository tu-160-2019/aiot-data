package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.exceptions.BinaryWriteFailedException;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BaseValueHandler<T> implements IValueHandler<T> {

  @Override
  public void handle(DataOutputStream buffer, final T value) {
    try {
      if (value == null) {
        buffer.writeInt(-1);
        return;
      }
      internalHandle(buffer, value);
    } catch (IOException e) {
      if (null != e.getCause()) {
        throw new BinaryWriteFailedException(e.getCause());
      } else {
        throw new BinaryWriteFailedException(e);
      }
    }
  }

  protected abstract void internalHandle(DataOutputStream buffer, final T value) throws IOException;
}
