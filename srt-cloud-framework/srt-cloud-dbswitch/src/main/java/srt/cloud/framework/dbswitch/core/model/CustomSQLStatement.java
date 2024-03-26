package srt.cloud.framework.dbswitch.core.model;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;

import java.util.List;
import java.util.Map;

/**
 * @ClassName CustomSQLStatement
 * @Author zrx
 * @Date 2023/8/27 11:54
 */
public class CustomSQLStatement implements SQLStatement {
	private String sql;
	public CustomSQLStatement(String sql) {
		this.sql = sql;
	}

	@Override
	public DbType getDbType() {
		return null;
	}

	@Override
	public boolean isAfterSemi() {
		return false;
	}

	@Override
	public void setAfterSemi(boolean b) {

	}

	@Override
	public void accept(SQLASTVisitor sqlastVisitor) {

	}

	@Override
	public SQLStatement clone() {
		return null;
	}

	@Override
	public SQLObject getParent() {
		return null;
	}

	@Override
	public void setParent(SQLObject sqlObject) {

	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public boolean containsAttribute(String s) {
		return false;
	}

	@Override
	public Object getAttribute(String s) {
		return null;
	}

	@Override
	public void putAttribute(String s, Object o) {

	}

	@Override
	public Map<String, Object> getAttributesDirect() {
		return null;
	}

	@Override
	public void output(StringBuffer stringBuffer) {

	}

	@Override
	public void output(Appendable appendable) {

	}

	@Override
	public void addBeforeComment(String s) {

	}

	@Override
	public void addBeforeComment(List<String> list) {

	}

	@Override
	public List<String> getBeforeCommentsDirect() {
		return null;
	}

	@Override
	public void addAfterComment(String s) {

	}

	@Override
	public void addAfterComment(List<String> list) {

	}

	@Override
	public List<String> getAfterCommentsDirect() {
		return null;
	}

	@Override
	public boolean hasBeforeComment() {
		return false;
	}

	@Override
	public boolean hasAfterComment() {
		return false;
	}

	@Override
	public List<SQLObject> getChildren() {
		return null;
	}

	@Override
	public List<SQLCommentHint> getHeadHintsDirect() {
		return null;
	}

	@Override
	public void setHeadHints(List<SQLCommentHint> list) {

	}

	@Override
	public String toString(VisitorFeature... visitorFeatures) {
		return null;
	}

	@Override
	public String toString() {
		return this.sql;
	}

	@Override
	public String toLowerCaseString() {
		return null;
	}

	@Override
	public String toParameterizedString() {
		return null;
	}
}
