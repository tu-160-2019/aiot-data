package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;

public class Inet4AddressValueHandler extends BaseValueHandler<Inet4Address> {

  private static final byte IPv4 = 2;
  private static final byte MASK = 32;
  private static final byte IS_CIDR = 0;

  @Override
  protected void internalHandle(DataOutputStream buffer, final Inet4Address value)
      throws IOException {
    buffer.writeInt(8);

    buffer.writeByte(IPv4);
    buffer.writeByte(MASK);
    buffer.writeByte(IS_CIDR);

    byte[] inet4AddressBytes = value.getAddress();

    buffer.writeByte(inet4AddressBytes.length);
    buffer.write(inet4AddressBytes);
  }

  @Override
  public int getLength(Inet4Address value) {
    return 8;
  }
}
