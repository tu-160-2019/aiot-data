package srt.cloud.framework.dbswitch.pgwriter.configuration;

public class Configuration implements IConfiguration {

  private final int bufferSize;

  public Configuration() {
    this(65536);
  }

  public Configuration(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  @Override
  public int getBufferSize() {
    return bufferSize;
  }
}
