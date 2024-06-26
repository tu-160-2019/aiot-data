package srt.cloud.framework.dbswitch.pgwriter.pgsql.handlers;

import srt.cloud.framework.dbswitch.pgwriter.pgsql.model.range.Range;

import java.io.DataOutputStream;
import java.io.IOException;

public class RangeValueHandler<TElementType> extends BaseValueHandler<Range<TElementType>> {

  private final IValueHandler<TElementType> valueHandler;

  public RangeValueHandler(IValueHandler<TElementType> valueHandler) {
    this.valueHandler = valueHandler;
  }

  @SuppressWarnings("NullAway") // infinite bound checks only pass when bound value is not null
  @Override
  protected void internalHandle(DataOutputStream buffer, Range<TElementType> value)
      throws IOException {
    buffer.writeInt(getLength(value));
    buffer.writeByte(value.getFlags());

    if (value.isEmpty()) {
      return;
    }

    if (!value.isLowerBoundInfinite()) {
      valueHandler.handle(buffer, value.getLowerBound());
    }

    if (!value.isUpperBoundInfinite()) {
      valueHandler.handle(buffer, value.getUpperBound());
    }
  }

  @SuppressWarnings("NullAway") // infinite bound checks only pass when bound value is not null
  @Override
  public int getLength(Range<TElementType> value) {
    int totalLen = 1;

    if (!value.isEmpty()) {
      if (!value.isLowerBoundInfinite()) {
        totalLen += 4 + valueHandler.getLength(value.getLowerBound());
      }

      if (!value.isUpperBoundInfinite()) {
        totalLen += 4 + valueHandler.getLength(value.getUpperBound());
      }
    }

    return totalLen;
  }
}

