package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatValueHandler<T extends Number> extends BaseValueHandler<T> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final T value) throws IOException {
    buffer.writeInt(4);
    buffer.writeFloat(value.floatValue());
  }

  @Override
  public int getLength(T value) {
    return 4;
  }
}
