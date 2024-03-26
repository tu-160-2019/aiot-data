package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.constants.DataType;

public interface IValueHandlerProvider {

  <TTargetType> IValueHandler<TTargetType> resolve(DataType targetType);
}
