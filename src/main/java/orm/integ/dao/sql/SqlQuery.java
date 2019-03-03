package orm.integ.dao.sql;

import orm.integ.utils.StringUtils;

public class SqlQuery extends QueryRequest {

	public SqlQuery(String sql, Object... values) {
		this.sql = sql;
		this.values = values==null?new Object[]{}:values;
	}

	private final String sql;
	
	private Object[] values;

	public String getSql() {
		return sql;
	}

	public Object[] getValues() {
		return values;
	}
	
	@Override
	public String toString() {
		if (values!=null&& values.length>0) {
			return sql + "|" + StringUtils.link(values, ",");
		}
		else {
			return sql;
		}
	}
	
	@Override
	public SqlQuery clone() {
		SqlQuery q = new SqlQuery(sql);
		q.copyFrom(this);
		q.values = this.values;
		return q;
	}

	@Override
	public int getCountQueryHashCode() {
		int code = sql.hashCode();
		for (Object v:this.values) {
			if (v!=null) {
				code = code*31+v.hashCode();
			}
		}
		return code;
	}

}
