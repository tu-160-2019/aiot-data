package srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric;

import java.util.List;

public class Polygon {

  private final List<Point> points;

  public Polygon(List<Point> points) {

    if (points == null) {
      throw new IllegalArgumentException("points");
    }

    this.points = points;
  }

  public List<Point> getPoints() {
    return points;
  }

  public int size() {
    return points.size();
  }
}
