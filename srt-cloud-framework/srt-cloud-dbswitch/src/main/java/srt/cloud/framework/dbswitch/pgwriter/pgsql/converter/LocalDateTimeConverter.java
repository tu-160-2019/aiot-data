package srt.cloud.framework.dbswitch.pgwriter.pgsql.converter;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.utils.TimeStampUtils;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements IValueConverter<LocalDateTime, Long> {

  @Override
  public Long convert(final LocalDateTime dateTime) {
    return TimeStampUtils.convertToPostgresTimeStamp(dateTime);
  }
}
