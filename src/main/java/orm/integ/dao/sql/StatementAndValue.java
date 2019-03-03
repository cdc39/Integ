package orm.integ.dao.sql;

import orm.integ.utils.StringUtils;

public class StatementAndValue {
	
	public StatementAndValue(String statement, Object...values) {
		this.statement = statement;
		this.values = values==null?new Object[]{}:values;
	}

	private final String statement;
	
	private final Object[] values;

	public String getStatement() {
		return statement;
	}

	public Object[] getValues() {
		return values;
	}
	
	@Override
	public String toString() {
		if (values!=null&& values.length>0) {
			return statement + "|" + StringUtils.link(values, ",");
		}
		else {
			return statement;
		}
	}
	
}
