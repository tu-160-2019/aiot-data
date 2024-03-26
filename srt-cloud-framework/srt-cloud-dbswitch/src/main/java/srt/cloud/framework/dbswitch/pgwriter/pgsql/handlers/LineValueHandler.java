package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric.Line;

import java.io.DataOutputStream;
import java.io.IOException;

public class LineValueHandler extends BaseValueHandler<Line> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Line value) throws IOException {
    buffer.writeInt(24);

    buffer.writeDouble(value.getA());
    buffer.writeDouble(value.getB());
    buffer.writeDouble(value.getC());
  }

  @Override
  public int getLength(Line value) {
    return 24;
  }
}
