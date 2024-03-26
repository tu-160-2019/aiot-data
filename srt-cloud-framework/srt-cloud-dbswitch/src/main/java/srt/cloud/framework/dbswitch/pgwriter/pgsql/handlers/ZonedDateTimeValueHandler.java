package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.IValueConverter;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.LocalDateTimeConverter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeValueHandler extends BaseValueHandler<ZonedDateTime> {

  private IValueConverter<ZonedDateTime, Long> dateTimeConverter;

  public ZonedDateTimeValueHandler() {
    this(new ToUTCStripTimezone());
  }

  public ZonedDateTimeValueHandler(IValueConverter<ZonedDateTime, Long> dateTimeConverter) {
    this.dateTimeConverter = dateTimeConverter;
  }

  @Override
  protected void internalHandle(DataOutputStream buffer, ZonedDateTime value) throws IOException {
    buffer.writeInt(8);
    buffer.writeLong(dateTimeConverter.convert(value));
  }

  @Override
  public int getLength(ZonedDateTime value) {
    return 8;
  }

  private static final class ToUTCStripTimezone implements IValueConverter<ZonedDateTime, Long> {

    private final IValueConverter<LocalDateTime, Long> converter = new LocalDateTimeConverter();

    @Override
    public Long convert(final ZonedDateTime value) {
      return converter.convert(value.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
  }
}
