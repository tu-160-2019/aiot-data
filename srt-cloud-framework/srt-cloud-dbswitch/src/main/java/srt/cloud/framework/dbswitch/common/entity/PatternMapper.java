// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.entity;

import java.util.Objects;

/**
 * 基于正则表达式的批评替换实体定义
 */
public class PatternMapper {

  private String fromPattern;
  private String toValue;

  public PatternMapper() {
  }

  public PatternMapper(String fromPattern, String toValue) {
    this.fromPattern = fromPattern;
    this.toValue = toValue;
  }

  public String getFromPattern() {
    return fromPattern;
  }

  public void setFromPattern(String fromPattern) {
    this.fromPattern = fromPattern;
  }

  public String getToValue() {
    return toValue;
  }

  public void setToValue(String toValue) {
    this.toValue = toValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PatternMapper that = (PatternMapper) o;
    return fromPattern.equals(that.fromPattern) && toValue.equals(that.toValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromPattern, toValue);
  }

  @Override
  public String toString() {
    return "PatternMapper{" +
        "fromPattern='" + fromPattern + '\'' +
        ", toValue='" + toValue + '\'' +
        '}';
  }

}
