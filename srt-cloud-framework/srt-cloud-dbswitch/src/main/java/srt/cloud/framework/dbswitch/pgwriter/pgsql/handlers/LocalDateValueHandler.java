package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.IValueConverter;
import srt.cloud.framework.dbswitch.pgwriter.pgsql.converter.LocalDateConverter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateValueHandler extends BaseValueHandler<LocalDate> {

  private IValueConverter<LocalDate, Integer> dateConverter;

  public LocalDateValueHandler() {
    this(new LocalDateConverter());
  }

  public LocalDateValueHandler(IValueConverter<LocalDate, Integer> dateTimeConverter) {
    this.dateConverter = dateTimeConverter;
  }

  @Override
  protected void internalHandle(DataOutputStream buffer, final LocalDate value) throws IOException {
    buffer.writeInt(4);
    buffer.writeInt(dateConverter.convert(value));
  }

  @Override
  public int getLength(LocalDate value) {
    return 4;
  }
}
