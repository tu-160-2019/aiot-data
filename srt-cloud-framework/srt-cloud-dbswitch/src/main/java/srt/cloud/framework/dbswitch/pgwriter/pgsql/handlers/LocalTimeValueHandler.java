package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.IValueConverter;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.LocalTimeConverter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeValueHandler extends BaseValueHandler<LocalTime> {

  private IValueConverter<LocalTime, Long> timeConverter;

  public LocalTimeValueHandler() {
    this(new LocalTimeConverter());
  }

  public LocalTimeValueHandler(IValueConverter<LocalTime, Long> timeConverter) {
    this.timeConverter = timeConverter;
  }

  @Override
  protected void internalHandle(DataOutputStream buffer, final LocalTime value) throws IOException {
    buffer.writeInt(8);
    buffer.writeLong(timeConverter.convert(value));
  }

  @Override
  public int getLength(LocalTime value) {
    return 8;
  }
}
