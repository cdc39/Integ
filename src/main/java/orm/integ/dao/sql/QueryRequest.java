package orm.integ.dao.sql;

public abstract class QueryRequest implements Cloneable {

	public static final int PAGE_QUERY_MAX_RETURN = 2000;
	
	public abstract QueryRequest clone();
	
	public abstract int getCountQueryHashCode() ;

	
	protected String tableName;
	
	protected String keyColumn;
	
	protected String orderStatement;

	protected int start = 1;
	
	protected int limit = 1000;
	
	protected int maxReturnRowNum = PAGE_QUERY_MAX_RETURN;
	
	protected OrderGroup order = new OrderGroup();
	
	protected String[] viewFields;
	
	public OrderGroup getOrder() {
		return order;
	}
	
	public void setTableInfo(TableInfo table) {
		this.tableName = table.getFullTableName();
		this.keyColumn = table.getKeyColumn();
	}
	
	public void setTableInfo(String tableName, String keyColumn) {
		this.tableName = tableName;
		this.keyColumn = keyColumn;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getKeyColumn() {
		return keyColumn;
	}
	
	public int getStart() {
		return start;
	}

	public void setPageInfo(int start, int limit) {
		if (start<=0) {
			start = 1;
		}
		this.start = start;
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	public int getLast() {
		int last = start+limit-1;
		if (last>maxReturnRowNum) {
			last = maxReturnRowNum;
		}
		return last;
	}
	
	public int getMaxReturnRowNum() {
		return maxReturnRowNum;
	}

	public void setMaxReturnRowNum(int maxReturnRowNum) {
		this.maxReturnRowNum = maxReturnRowNum;
	}
	
	public void setOrder(String orderStatement) {
		this.orderStatement = orderStatement;
		this.order = OrderGroup.parse(orderStatement);
	}
	
	public abstract Object[] getValues() ;
	

	public String[] getViewFields() {
		return viewFields;
	}

	public void setViewFields(String[] viewFields) {
		this.viewFields = viewFields;
	}

	public void copyFrom(QueryRequest req) {
		this.tableName = req.tableName;
		this.keyColumn = req.keyColumn;
		this.order = req.order;
		this.start = req.start;
		this.limit = req.limit;
		this.maxReturnRowNum = req.maxReturnRowNum;
	}
	
	@Override
	public int hashCode() {
		int code = getCountQueryHashCode();
		String orderStmt = order.toString();
		if (orderStmt!=null) {
			code = code*31 + orderStmt.hashCode();
		}
		code = code*11 + start;
		code = code*11 + limit;
		return code;
	}
	
}
