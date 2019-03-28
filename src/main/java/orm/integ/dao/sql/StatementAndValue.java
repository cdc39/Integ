package orm.integ.dao.sql;

import java.util.ArrayList;
import java.util.List;

import orm.integ.utils.StringUtils;

public class StatementAndValue {
	
	public StatementAndValue(String statement, Object...values) {
		this.statement = statement;
		this.values = values==null?new Object[]{}:values;
	}

	private final String statement;
	
	private final Object[] values;

	public String getStatement() {
		return getStatement(false);
	}
	
	public String getStatement(boolean withAnd) {
		if (isEmpty()) {
			return " ";
		}
		else if (withAnd) {
			return " and "+statement;
		}
		else {
			return statement;
		}
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
	
	private boolean isEmpty() {
		return statement==null || statement.trim().length()==0;
	}

	public static Object[] unionValues(StatementAndValue... wheres) {
		List<Object> valueAll = new ArrayList<>();
		for (StatementAndValue where: wheres) {
			if (where.getValues()!=null) {
				for (Object val: where.getValues()) {
					valueAll.add(val);
				}
			}
		}
		return valueAll.toArray();
	}
	
}
