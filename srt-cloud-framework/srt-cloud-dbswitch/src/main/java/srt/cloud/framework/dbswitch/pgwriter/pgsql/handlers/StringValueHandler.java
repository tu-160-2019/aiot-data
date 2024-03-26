package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.util.StringUtils;

import java.io.DataOutputStream;
import java.io.IOException;

public class StringValueHandler extends BaseValueHandler<String> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final String value) throws IOException {
    byte[] utf8Bytes = StringUtils.getUtf8Bytes(value);
    buffer.writeInt(utf8Bytes.length);
    buffer.write(utf8Bytes);
  }

  @Override
  public int getLength(String value) {
    byte[] utf8Bytes = StringUtils.getUtf8Bytes(value);
    return utf8Bytes.length;
  }
}
