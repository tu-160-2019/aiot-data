package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleValueHandler<T extends Number> extends BaseValueHandler<T> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final T value) throws IOException {
    buffer.writeInt(8);
    buffer.writeDouble(value.doubleValue());
  }

  @Override
  public int getLength(T value) {
    return 8;
  }
}
