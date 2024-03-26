// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.model;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库列描述符信息定义(Column Description)
 *
 * @author jrl
 */
public class ColumnDescription {

	private String fieldName;
	private String labelName;
	private String fieldTypeName;
	private String filedTypeClassName;

	private Integer valType;
	private Integer length;
	private Integer precision;

	private int fieldType;
	private int displaySize;
	private int scaleSize;
	private int precisionSize;
	private boolean isAutoIncrement;
	private boolean isNullable;
	private String remarks;
	private boolean signed = false;
	private ProductTypeEnum dbtype;
	//索引是否可以不唯一
	private boolean nonIndexUnique;
	//索引类别
	private String indexQualifier;
	//索引名称
	private String indexName;
	private List<String> indexes;
	private short indexType;
	private String ascOrDesc;
	//默认值
	private String defaultValue;
	//是否是主键
	private boolean isPk;

	public String getFieldName() {
		if (null != this.fieldName) {
			return fieldName;
		}

		return this.labelName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getLabelName() {
		if (null != labelName) {
			return labelName;
		}

		return this.fieldName;
	}

	public void setLabelName(String labalName) {
		this.labelName = labalName;
	}

	public String getFieldTypeName() {
		return fieldTypeName;
	}

	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}

	public String getFiledTypeClassName() {
		return filedTypeClassName;
	}

	public void setFiledTypeClassName(String filedTypeClassName) {
		this.filedTypeClassName = filedTypeClassName;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public Integer getValType() {
		return valType;
	}

	public void setValType(Integer valType) {
		this.valType = valType;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public int getScaleSize() {
		return scaleSize;
	}

	public void setScaleSize(int scaleSize) {
		this.scaleSize = scaleSize;
	}

	public int getPrecisionSize() {
		return precisionSize;
	}

	public void setPrecisionSize(int precisionSize) {
		this.precisionSize = precisionSize;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public ProductTypeEnum getDbType() {
		return this.dbtype;
	}

	public void setDbType(ProductTypeEnum dbtype) {
		this.dbtype = dbtype;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setNonIndexUnique(boolean nonIndexUnique) {
		this.nonIndexUnique = nonIndexUnique;
	}

	public boolean isNonIndexUnique() {
		return nonIndexUnique;
	}

	public String getIndexQualifier() {
		return indexQualifier;
	}

	public void setIndexQualifier(String indexQualifier) {
		this.indexQualifier = indexQualifier;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public short getIndexType() {
		return indexType;
	}

	public void setIndexType(short indexType) {
		this.indexType = indexType;
	}

	public String getAscOrDesc() {
		return ascOrDesc;
	}

	public void setAscOrDesc(String ascOrDesc) {
		this.ascOrDesc = ascOrDesc;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isPk() {
		return isPk;
	}

	public void setPk(boolean pk) {
		isPk = pk;
	}

	public List<String> getIndexes() {
		if (indexes == null) {
			setIndexes(new ArrayList<>());
		}
		return indexes;
	}

	public void setIndexes(List<String> indexes) {
		this.indexes = indexes;
	}

	public ColumnDescription copy() {
		ColumnDescription description = new ColumnDescription();
		description.setFieldName(fieldName);
		description.setLabelName(labelName);
		description.setFieldTypeName(fieldTypeName);
		description.setFiledTypeClassName(filedTypeClassName);
		description.setFieldType(fieldType);
		description.setDisplaySize(displaySize);
		description.setScaleSize(scaleSize);
		description.setPrecisionSize(precisionSize);
		description.setAutoIncrement(isAutoIncrement);
		description.setNullable(isNullable);
		description.setRemarks(remarks);
		description.setSigned(signed);
		description.setDbType(dbtype);
		description.setNonIndexUnique(nonIndexUnique);
		description.setIndexQualifier(indexQualifier);
		description.setIndexName(indexName);
		description.setIndexes(indexes);
		description.setIndexType(indexType);
		description.setAscOrDesc(ascOrDesc);
		description.setDefaultValue(defaultValue);
		description.setPk(isPk);
		return description;
	}

	/////////////////////////////////////////////

	public ColumnMetaData getMetaData() {
		return new ColumnMetaData(this);
	}
}
