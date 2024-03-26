package srt.cloud.framework.dbswitch.pgwriter.function;

@FunctionalInterface
public interface ToBooleanFunction<T> {

  /**
   * Applies this function to the given argument.
   *
   * @param value the function argument
   * @return the function result
   */
  boolean applyAsBoolean(T value);
}
