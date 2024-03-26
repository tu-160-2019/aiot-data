// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.calcite;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.TimeUnitRange;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.dialect.PostgresqlSqlDialect;
import org.apache.calcite.sql.fun.SqlFloorFunction;
import org.apache.calcite.sql.fun.SqlRowOperator;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * 这里重写了PostgresqlSqlDialect的unparseCall()方法
 *
 * @author jrl
 */
public class ThePostgresqlSqlDialect extends PostgresqlSqlDialect {

  /**
   * PostgreSQL type system.
   */
  private static final RelDataTypeSystem POSTGRESQL_TYPE_SYSTEM = new RelDataTypeSystemImpl() {
    @Override
    public int getMaxPrecision(SqlTypeName typeName) {
      switch (typeName) {
        case VARCHAR:
          // From htup_details.h in postgresql:
          // MaxAttrSize is a somewhat arbitrary upper limit on the declared size of
          // data fields of char(n) and similar types. It need not have anything
          // directly to do with the *actual* upper limit of varlena values, which
          // is currently 1Gb (see TOAST structures in postgres.h). I've set it
          // at 10Mb which seems like a reasonable number --- tgl 8/6/00. */
          return 10 * 1024 * 1024;
        default:
          return super.getMaxPrecision(typeName);
      }
    }
  };

  public static final SqlDialect DEFAULT = new ThePostgresqlSqlDialect(
      EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.POSTGRESQL).withIdentifierQuoteString("\"")
          .withUnquotedCasing(Casing.TO_LOWER).withDataTypeSystem(POSTGRESQL_TYPE_SYSTEM));

  /**
   * Creates a PostgresqlSqlDialect.
   */
  public ThePostgresqlSqlDialect(Context context) {
    super(context);
  }

  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    switch (call.getKind()) {
      case FLOOR:
        if (call.operandCount() != 2) {
          super.unparseCall(writer, call, leftPrec, rightPrec);
          return;
        }

        final SqlLiteral timeUnitNode = call.operand(1);
        final TimeUnitRange timeUnit = timeUnitNode.getValueAs(TimeUnitRange.class);

        SqlCall call2 = SqlFloorFunction.replaceTimeUnitOperand(call, timeUnit.name(),
            timeUnitNode.getParserPosition());
        SqlFloorFunction.unparseDatetimeFunction(writer, call2, "DATE_TRUNC", false);
        break;

      default:
        SqlOperator operator = call.getOperator();
        if (operator instanceof SqlRowOperator) {
          SqlUtil.unparseFunctionSyntax(new TheSqlRowOperator(), writer, call);
        } else {
          super.unparseCall(writer, call, leftPrec, rightPrec);
        }
        break;

    }
  }

  /**
   * Appends a string literal to a buffer.
   *
   * @param buf         Buffer
   * @param charsetName Character set name, e.g. "utf16", or null
   * @param val         String value
   */
  @Override
  public void quoteStringLiteral(StringBuilder buf, String charsetName, String val) {
    buf.append(literalQuoteString);
    buf.append(val.replace(literalEndQuoteString, literalEscapedQuote));
    buf.append(literalEndQuoteString);
  }

}
