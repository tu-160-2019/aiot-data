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
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.fun.SqlFloorFunction;
import org.apache.calcite.sql.fun.SqlLibraryOperators;
import org.apache.calcite.sql.fun.SqlRowOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * 这里重写了OracleSqlDialect的unparseCall()方法
 *
 * @author jrl
 */
public class TheOracleSqlDialect extends OracleSqlDialect {

  /**
   * OracleDB type system.
   */
  private static final RelDataTypeSystem ORACLE_TYPE_SYSTEM = new RelDataTypeSystemImpl() {
    @Override
    public int getMaxPrecision(SqlTypeName typeName) {
      switch (typeName) {
        case VARCHAR:
          // Maximum size of 4000 bytes for varchar2.
          return 4000;
        default:
          return super.getMaxPrecision(typeName);
      }
    }
  };

  public static final SqlDialect DEFAULT = new TheOracleSqlDialect(
      EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.ORACLE).withIdentifierQuoteString("\"")
          .withDataTypeSystem(ORACLE_TYPE_SYSTEM));

  /**
   * Creates an OracleSqlDialect.
   */
  public TheOracleSqlDialect(Context context) {
    super(context);
  }

  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    if (call.getOperator() == SqlStdOperatorTable.SUBSTRING) {
      SqlUtil.unparseFunctionSyntax(SqlLibraryOperators.SUBSTR, writer, call);
    } else {
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
          SqlFloorFunction.unparseDatetimeFunction(writer, call2, "TRUNC", true);
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

// End OracleSqlDialect.java
