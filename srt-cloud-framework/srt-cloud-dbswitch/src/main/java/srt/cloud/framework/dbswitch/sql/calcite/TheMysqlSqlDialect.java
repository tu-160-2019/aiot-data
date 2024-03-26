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
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.fun.SqlRowOperator;

/**
 * 这里重写了MysqlSqlDialect的unparseCall()方法
 *
 * @author jrl
 */
public class TheMysqlSqlDialect extends MysqlSqlDialect {

  public static final SqlDialect DEFAULT = new TheMysqlSqlDialect(
      EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.MYSQL).withIdentifierQuoteString("`")
          .withUnquotedCasing(Casing.UNCHANGED).withNullCollation(NullCollation.LOW));

  public TheMysqlSqlDialect(Context context) {
    super(context);
  }

  /**
   * Unparses datetime floor for MySQL. There is no TRUNC function, so simulate this using calls to
   * DATE_FORMAT.
   *
   * @param writer Writer
   * @param call   Call
   */
  private void unparseFloor(SqlWriter writer, SqlCall call) {
    SqlLiteral node = call.operand(1);
    TimeUnitRange unit = (TimeUnitRange) node.getValue();

    if (unit == TimeUnitRange.WEEK) {
      writer.print("STR_TO_DATE");
      SqlWriter.Frame frame = writer.startList("(", ")");

      writer.print("DATE_FORMAT(");
      call.operand(0).unparse(writer, 0, 0);
      writer.print(", '%x%v-1'), '%x%v-%w'");
      writer.endList(frame);
      return;
    }

    String format;
    switch (unit) {
      case YEAR:
        format = "%Y-01-01";
        break;
      case MONTH:
        format = "%Y-%m-01";
        break;
      case DAY:
        format = "%Y-%m-%d";
        break;
      case HOUR:
        format = "%Y-%m-%d %H:00:00";
        break;
      case MINUTE:
        format = "%Y-%m-%d %H:%i:00";
        break;
      case SECOND:
        format = "%Y-%m-%d %H:%i:%s";
        break;
      default:
        throw new AssertionError("MYSQL does not support FLOOR for time unit: " + unit);
    }

    writer.print("DATE_FORMAT");
    SqlWriter.Frame frame = writer.startList("(", ")");
    call.operand(0).unparse(writer, 0, 0);
    writer.sep(",", true);
    writer.print("'" + format + "'");
    writer.endList(frame);
  }

  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
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
          // 这里处理INSERT语句的ROW关键词问题
          SqlUtil.unparseFunctionSyntax(new TheSqlRowOperator(), writer, call);
        } else if (call instanceof SqlOrderBy) {
          // 这里处理分页的LIMIT OFFSET问题
          SqlOrderBy thecall = (SqlOrderBy) call;
          TheSqlOrderBy newcall = new TheSqlOrderBy(call.getParserPosition(), thecall.query,
              thecall.orderList,
              thecall.offset, thecall.fetch);
          newcall.getOperator().unparse(writer, newcall, leftPrec, rightPrec);
          // TheSqlOrderBy.OPERATOR.unparse(writer, thecall, leftPrec, rightPrec);
        } else {
          // 其他情况走这里
          operator.unparse(writer, call, leftPrec, rightPrec);
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
