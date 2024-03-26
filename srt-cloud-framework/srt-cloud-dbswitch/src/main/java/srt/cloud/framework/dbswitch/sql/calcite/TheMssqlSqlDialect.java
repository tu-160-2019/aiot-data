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

import org.apache.calcite.avatica.util.TimeUnitRange;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.dialect.MssqlSqlDialect;
import org.apache.calcite.sql.fun.SqlRowOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.ReturnTypes;

/**
 * 这里重写了MssqlSqlDialect的unparseCall()方法
 *
 * @author jrl
 */
public class TheMssqlSqlDialect extends MssqlSqlDialect {

  public static final SqlDialect DEFAULT = new TheMssqlSqlDialect(EMPTY_CONTEXT
      .withDatabaseProduct(DatabaseProduct.MSSQL).withIdentifierQuoteString("[")
      .withCaseSensitive(false));

  private static final SqlFunction MSSQL_SUBSTRING = new SqlFunction("SUBSTRING",
      SqlKind.OTHER_FUNCTION,
      ReturnTypes.ARG0_NULLABLE_VARYING, null, null, SqlFunctionCategory.STRING);

  /**
   * Creates a MssqlSqlDialect.
   */
  public TheMssqlSqlDialect(Context context) {
    super(context);
  }

  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    if (call.getOperator() == SqlStdOperatorTable.SUBSTRING) {
      if (call.operandCount() != 3) {
        throw new IllegalArgumentException("MSSQL SUBSTRING requires FROM and FOR arguments");
      }
      SqlUtil.unparseFunctionSyntax(MSSQL_SUBSTRING, writer, call);
    } else {
      switch (call.getKind()) {
        case FLOOR:
          if (call.operandCount() != 2) {
            super.unparseCall(writer, call, leftPrec, rightPrec);
            return;
          }
          unparseFloor(writer, call);
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
  }

  /**
   * Unparses datetime floor for Microsoft SQL Server. There is no TRUNC function, so simulate this
   * using calls to CONVERT.
   *
   * @param writer Writer
   * @param call   Call
   */
  private void unparseFloor(SqlWriter writer, SqlCall call) {
    SqlLiteral node = call.operand(1);
    TimeUnitRange unit = (TimeUnitRange) node.getValue();

    switch (unit) {
      case YEAR:
        unparseFloorWithUnit(writer, call, 4, "-01-01");
        break;
      case MONTH:
        unparseFloorWithUnit(writer, call, 7, "-01");
        break;
      case WEEK:
        writer.print(
            "CONVERT(DATETIME, CONVERT(VARCHAR(10), " + "DATEADD(day, - (6 + DATEPART(weekday, ");
        call.operand(0).unparse(writer, 0, 0);
        writer.print(")) % 7, ");
        call.operand(0).unparse(writer, 0, 0);
        writer.print("), 126))");
        break;
      case DAY:
        unparseFloorWithUnit(writer, call, 10, "");
        break;
      case HOUR:
        unparseFloorWithUnit(writer, call, 13, ":00:00");
        break;
      case MINUTE:
        unparseFloorWithUnit(writer, call, 16, ":00");
        break;
      case SECOND:
        unparseFloorWithUnit(writer, call, 19, ":00");
        break;
      default:
        throw new IllegalArgumentException("MSSQL does not support FLOOR for time unit: " + unit);
    }
  }

  private void unparseFloorWithUnit(SqlWriter writer, SqlCall call, int charLen, String offset) {
    writer.print("CONVERT");
    SqlWriter.Frame frame = writer.startList("(", ")");
    writer.print("DATETIME, CONVERT(VARCHAR(" + charLen + "), ");
    call.operand(0).unparse(writer, 0, 0);
    writer.print(", 126)");

    if (offset.length() > 0) {
      writer.print("+'" + offset + "'");
    }
    writer.endList(frame);
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
