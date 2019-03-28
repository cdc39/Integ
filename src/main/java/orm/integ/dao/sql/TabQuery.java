package orm.integ.dao.sql;

public class TabQuery extends QueryRequest {
	
	private Where where = new Where();
	
	private TabQuery joinQuery;
	
	private String joinByField;
	
	public TabQuery() {
	}
	
	public TabQuery(TableInfo tab) {
		this.setTableInfo(tab);
	}

	public Where getWhere() {
		return where;
	}
	
	public void setWhere(Where where) {
		this.where = where;
	}
	
	@Override
	public Object[] getValues() {
		return where.getValues();
	}
	
	public void addWhereItem(String whereStmt, Object...values) {
		where.addItem(whereStmt, values);
	}
	
	@Override
	public TabQuery clone() {
		TabQuery q = new TabQuery();
		q.copyFrom(this);
		q.getWhere().copyFrom(this.getWhere());
		return q;
	}
	
	@Override
	public int getCountQueryHashCode() {
		int code = tableName.hashCode();
		code = code*31 + where.hashCode();
		code = code*31 + order.hashCode();
		return code;		
	}

	public void setJoinQuery(TabQuery joinQuery, String joinByField) {
		this.joinByField = joinByField;
		this.joinQuery = joinQuery;
	}

	public TabQuery getJoinQuery() {
		return joinQuery;
	}

	public String getJoinByField() {
		return joinByField;
	}

	
}
