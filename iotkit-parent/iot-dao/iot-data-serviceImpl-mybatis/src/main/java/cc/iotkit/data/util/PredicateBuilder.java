package cc.iotkit.data.util;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.function.Supplier;

/**
 * @author: Longjun.Tu
 * @description:
 * @date:created in 2023/5/26 17:02
 * @modificed by:
 */
public class PredicateBuilder {
  /**
   * 1 = 1, 永远为真.
   */
  private static final BooleanExpression ALWAYS_TRUE = Expressions.ONE.eq(Expressions.ONE);

  private BooleanExpression expression;

  private PredicateBuilder(BooleanExpression expression) {
    this.expression = expression;
  }

  /**
   * 获取{@link PredicateBuilder}实例.
   *
   * @return PredicateBuilder
   */
  public static PredicateBuilder instance() {
    return instance(null);
  }

  /**
   * 获取{@link PredicateBuilder}实例.
   *
   * @param init 初始条件
   * @return PredicateBuilder
   */
  public static PredicateBuilder instance(BooleanExpression init) {
    return new PredicateBuilder(init);
  }

  /**
   * 使用 'and' 对条件进行拼接
   *
   * @param expr Boolean expressions
   * @return PredicateBuilder
   */
  public PredicateBuilder and(BooleanExpression expr) {
    return and(true, () -> expr);
  }

  /**
   * 如果条件为true, 则使用 'and' 对条件进行拼接
   *
   * @param condition    执行条件, 如果为false, 则不会拼接该条件.
   * @param exprSupplier expression supplier.
   * @return PredicateBuilder
   */
  public PredicateBuilder and(boolean condition, Supplier<BooleanExpression> exprSupplier) {
    if (condition) {
      if (exprIsNull()) {
        expression = exprSupplier.get();
        return this;
      }
      expression = expression.and(exprSupplier.get());
    }
    return this;
  }

  /**
   * 使用 'or' 对条件进行拼接
   *
   * @param expr Boolean expressions
   * @return PredicateBuilder
   */
  public PredicateBuilder or(BooleanExpression expr) {
    return or(true, () -> expr);
  }

  /**
   * 如果条件为true, 则使用 'or' 对条件进行拼接
   *
   * @param condition    执行条件, 如果为false, 则不会拼接该条件.
   * @param exprSupplier expression supplier.
   * @return PredicateBuilder
   */
  public PredicateBuilder or(boolean condition, Supplier<BooleanExpression> exprSupplier) {
    if (condition) {
      if (exprIsNull()) {
        expression = exprSupplier.get();
        return this;
      }
      expression = expression.or(exprSupplier.get());
    }
    return this;
  }

  /**
   * 获取最终的{@link BooleanExpression}表达式
   *
   * @return Predicate, 如果没有拼接条件, 则默认返回 1 = 1的表达式.
   */
  public Predicate build() {
    if (exprIsNull()) {
      return ALWAYS_TRUE;
    }
    return expression;
  }

  private boolean exprIsNull() {
    return null == expression;
  }
}
