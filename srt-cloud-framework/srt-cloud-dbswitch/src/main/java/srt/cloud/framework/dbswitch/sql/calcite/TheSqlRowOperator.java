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

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.util.Pair;

import java.util.AbstractList;
import java.util.Map;

/**
 * 代码来源于org.apache.calcite.sql.fun.SqlRowOperator的代码，这里 重写了unparse()方法，以处理INSERT语句的ROW问题。
 *
 * @author jrl
 */
public class TheSqlRowOperator extends SqlSpecialOperator {
  // ~ Constructors -----------------------------------------------------------

  public TheSqlRowOperator() {
    super("", SqlKind.ROW, MDX_PRECEDENCE, false, null, InferTypes.RETURN_TYPE,
        OperandTypes.VARIADIC);
  }

  // ~ Methods ----------------------------------------------------------------

  // implement SqlOperator
  @Override
  public SqlSyntax getSyntax() {
    // Function syntax would work too.
    return SqlSyntax.SPECIAL;
  }

  @Override
  public RelDataType inferReturnType(final SqlOperatorBinding opBinding) {
    // The type of a ROW(e1,e2) expression is a record with the types
    // {e1type,e2type}. According to the standard, field names are
    // implementation-defined.
    return opBinding.getTypeFactory()
        .createStructType(new AbstractList<Map.Entry<String, RelDataType>>() {

          @Override
          public Map.Entry<String, RelDataType> get(int index) {
            return Pair.of(SqlUtil.deriveAliasFromOrdinal(index), opBinding.getOperandType(index));
          }

          @Override
          public int size() {
            return opBinding.getOperandCount();
          }
        });
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    SqlUtil.unparseFunctionSyntax(this, writer, call);
  }

  // override SqlOperator
  @Override
  public boolean requiresDecimalExpansion() {
    return false;
  }
}

// End TheSqlRowOperator.java
