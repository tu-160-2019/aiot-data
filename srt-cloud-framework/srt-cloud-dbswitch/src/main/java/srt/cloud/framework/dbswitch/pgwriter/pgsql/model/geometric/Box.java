package srt.cloud.framework.dbswitch.pgwriter.pgsql.model.geometric;

public class Box {

  private final Point high;
  private final Point low;

  public Box(Point high, Point low) {
    if (high == null) {
      throw new IllegalArgumentException("high");
    }
    if (low == null) {
      throw new IllegalArgumentException("low");
    }
    this.high = high;
    this.low = low;
  }

  public Point getHigh() {
    return high;
  }

  public Point getLow() {
    return low;
  }

}
