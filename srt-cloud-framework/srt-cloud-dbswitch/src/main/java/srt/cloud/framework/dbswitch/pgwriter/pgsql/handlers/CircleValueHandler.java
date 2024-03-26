package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers.utils.GeometricUtils;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric.Circle;

import java.io.DataOutputStream;
import java.io.IOException;

public class CircleValueHandler extends BaseValueHandler<Circle> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Circle value) throws IOException {
    buffer.writeInt(24);
    // First encode the Center Point:
    GeometricUtils.writePoint(buffer, value.getCenter());
    // ... and then the Radius:
    buffer.writeDouble(value.getRadius());
  }

  @Override
  public int getLength(Circle value) {
    return 24;
  }
}
