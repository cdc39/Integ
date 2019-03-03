package orm.integ.dao.sql;

public class TabQuery extends QueryRequest {
	
	private WhereGroup where = new WhereGroup();
	
	public TabQuery() {
	}
	
	public TabQuery(TableInfo tab) {
		this.setTableInfo(tab);
	}

	public WhereGroup getWhere() {
		return where;
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
	
}
