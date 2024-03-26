package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class UUIDValueHandler extends BaseValueHandler<UUID> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final UUID value) throws IOException {
    buffer.writeInt(16);

    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(value.getMostSignificantBits());
    bb.putLong(value.getLeastSignificantBits());

    buffer.writeInt(bb.getInt(0));
    buffer.writeShort(bb.getShort(4));
    buffer.writeShort(bb.getShort(6));

    buffer.write(Arrays.copyOfRange(bb.array(), 8, 16));
  }

  @Override
  public int getLength(UUID value) {
    return 16;
  }
}
