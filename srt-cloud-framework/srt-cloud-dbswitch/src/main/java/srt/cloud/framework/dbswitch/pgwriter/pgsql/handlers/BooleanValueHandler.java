package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

public class BooleanValueHandler extends BaseValueHandler<Boolean> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Boolean value) throws IOException {
    buffer.writeInt(1);
    if (value) {
      buffer.writeByte(1);
    } else {
      buffer.writeByte(0);
    }
  }

  @Override
  public int getLength(Boolean value) {
    return 1;
  }
}
