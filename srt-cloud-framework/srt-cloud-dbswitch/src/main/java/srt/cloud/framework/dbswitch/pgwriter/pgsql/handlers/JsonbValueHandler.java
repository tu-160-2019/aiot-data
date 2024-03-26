package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

public class JsonbValueHandler extends BaseValueHandler<String> {

  private final int jsonbProtocolVersion;

  public JsonbValueHandler() {
    this(1);
  }

  public JsonbValueHandler(int jsonbProtocolVersion) {
    this.jsonbProtocolVersion = jsonbProtocolVersion;
  }

  @Override
  protected void internalHandle(DataOutputStream buffer, final String value) throws IOException {

    byte[] utf8Bytes = value.getBytes("UTF-8");

    // Write the Length of the Data to Copy:
    buffer.writeInt(utf8Bytes.length + 1);
    // Write the Jsonb Protocol Version:
    buffer.writeByte(jsonbProtocolVersion);
    // Copy the Data:
    buffer.write(utf8Bytes);
  }

  @Override
  public int getLength(String value) {
    throw new UnsupportedOperationException();
  }
}
