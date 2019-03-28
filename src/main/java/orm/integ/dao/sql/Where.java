package orm.integ.dao.sql;

import java.util.ArrayList;
import java.util.List;

import orm.integ.utils.StringUtils;

public class Where {

	private List<StatementAndValue> whereItems = new ArrayList<>();
	
	private Object[] values;
	
	public Where() {
	}
	
	public Where(String where, Object...values) {
		if (!isNull(where)) {
			this.addItem(new StatementAndValue(where, values));
		}
	}
	
	private void resetValues() {
		List<Object> valueList = new ArrayList<>();
		for (StatementAndValue stmt: this.whereItems) {
			for (Object value: stmt.getValues()) {
				if (value!=null && value instanceof String) {
					value = value.toString().trim();
				}
				valueList.add(value);
			}
		}
		this.values = valueList.toArray();
	}
	
	public Object[] getValues() {
		return values;
	}
	
	public List<StatementAndValue> getWhereItems() {
		return whereItems;
	}
	
	public void addItem(StatementAndValue item) {
		whereItems.add(item);
		resetValues();
	}
	
	public void addItem(String whereStmt, Object...values) {
		addItem(new StatementAndValue(whereStmt, values));
	}
	
	public String itemsToStatement() {
		List<String> stmtList = new ArrayList<>();
		if (whereItems.size()==1) {
			return whereItems.get(0).getStatement();
		}
		else if (whereItems.size()==0) {
			return "";
		}
		else {
			for (StatementAndValue stmt: whereItems) {
				if (!isNull(stmt.getStatement())) {
					stmtList.add("("+stmt.getStatement()+")");
				}
			}
			return StringUtils.link(stmtList, " and ");
		}
		
	}
	
	public String toString() {
		String whereStmt = itemsToStatement();
		if (isNull(whereStmt)) {
			whereStmt = " ";
		}
		else {
			if (whereStmt.toLowerCase().trim().indexOf("where")!=0) {
				whereStmt = " where "+whereStmt+" ";
			}
		}
		return whereStmt;
	}
	
	public StatementAndValue toStatementAndValue() {
		String where = this.itemsToStatement();
		return new StatementAndValue(where, values);
	}
	
	public StatementAndValue toStatementAndValue(String alias) {
		StatementAndValue newItem;
		Where where = new Where();
		for (StatementAndValue item:whereItems) {
			newItem = new StatementAndValue(alias+"."+item.getStatement(), item.getValues());
			where.addItem(newItem);
		}
		return where.toStatementAndValue();
	}
	
	private boolean isNull(String s) {
		return s==null||s.trim().equals("");
	}

	public void copyFrom(Where where) {
		this.whereItems.clear();
		this.whereItems.addAll(where.whereItems);
		this.resetValues();
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (StatementAndValue item:whereItems) {
			code = code*31+item.getStatement().hashCode();
		}
		if (this.values!=null) {
			for (Object v:this.values) {
				if (v!=null) {
					code = code*31+v.hashCode();
				}
			}
		}
		return code;
	}
	
}
