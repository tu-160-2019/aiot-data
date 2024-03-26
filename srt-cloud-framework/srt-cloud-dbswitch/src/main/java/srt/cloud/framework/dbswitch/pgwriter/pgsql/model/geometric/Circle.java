package srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric;

public class Circle {

  private final Point center;
  private final double radius;

  public Circle(Point center, double radius) {
    if (center == null) {
      throw new IllegalArgumentException("center");
    }
    this.center = center;
    this.radius = radius;
  }

  public Point getCenter() {
    return center;
  }

  public double getRadius() {
    return radius;
  }

}
