package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import java.io.DataOutputStream;

public interface IValueHandler<TTargetType> extends ValueHandler {

  void handle(DataOutputStream buffer, final TTargetType value);

  int getLength(final TTargetType value);
}
