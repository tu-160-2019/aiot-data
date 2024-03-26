package srt.cloud.framework.dbswitch.pgwriter.pgsql.converter;

import java.time.LocalTime;

public class LocalTimeConverter implements IValueConverter<LocalTime, Long> {

  @Override
  public Long convert(final LocalTime time) {
    return time.toNanoOfDay() / 1000L;
  }

}
