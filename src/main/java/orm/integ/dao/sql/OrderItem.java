package orm.integ.dao.sql;

public class OrderItem {
	
	public static OrderItem parseItem(String item) {
		if (item==null || item.trim().length()==0) {
			return null;
		}
		String colName;
		boolean desc = false;
		if (item.endsWith(" desc")) {
			colName = item.substring(0, item.length()-5).trim();
			desc = true;
		}
		else if (item.endsWith(" asc")){
			colName = item.substring(0,  item.length()-4).trim();
		}
		else {
			colName = item.trim();
		}
		return new OrderItem(colName, desc);
	}
	
	public OrderItem(String colName, boolean desc) {
		this.columnName = colName;
		this.desc = desc;
	}
	
	private String columnName;
	
	private boolean desc;

	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}
	
	public String toString() {
		if (desc) {
			return columnName + " desc";
		}
		else {
			return columnName;
		}
	}
	
}
