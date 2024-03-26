package srt.cloud.framework.dbswitch.pgwriter.pgsql.converter;

public interface IValueConverter<TSource, TTarget> {

  TTarget convert(TSource source);

}
