package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers.utils.GeometricUtils;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric.Box;

import java.io.DataOutputStream;
import java.io.IOException;

public class BoxValueHandler extends BaseValueHandler<Box> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Box value) throws IOException {
    buffer.writeInt(32);

    GeometricUtils.writePoint(buffer, value.getHigh());
    GeometricUtils.writePoint(buffer, value.getLow());
  }

  @Override
  public int getLength(Box value) {
    return 32;
  }
}
