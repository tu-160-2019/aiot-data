package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers.utils.GeometricUtils;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric.LineSegment;

import java.io.DataOutputStream;
import java.io.IOException;

public class LineSegmentValueHandler extends BaseValueHandler<LineSegment> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final LineSegment value)
      throws IOException {
    buffer.writeInt(32);

    GeometricUtils.writePoint(buffer, value.getP1());
    GeometricUtils.writePoint(buffer, value.getP2());
  }

  @Override
  public int getLength(LineSegment value) {
    return 32;
  }
}
