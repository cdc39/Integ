package orm.integ.dao.sql;

public abstract class SqlBuilder {

	public SqlBuilder() {
	}
	
	public abstract String getTestSql() ;
	
	public abstract String makePageQuerySql(QueryRequest req) ;
	
	public String makePageQueryIdsSql(QueryRequest req) {
		String sql = makePageQuerySql(req);
		return sql.replaceAll("\\*", req.getKeyColumn());
	}
	
	public String makeQuerySql(QueryRequest req) {
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			String whereStmt = tq.getWhere().toString();
			String orderStmt = req.getOrder().toString();
			String sql = "select * from "+tq.getTableName()+whereStmt+orderStmt;
			return sql;
		}
		else {
			SqlQuery sr = (SqlQuery)req;
			return sr.getSql();
		}
	}
	
	public String makeQueryIdsSql(QueryRequest req) {
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			String whereStmt = tq.getWhere().toString();
			String orderStmt = req.getOrder().toString();
			return "select "+tq.getKeyColumn()+" from "+tq.getTableName()+whereStmt+orderStmt;
		}
		else {
			SqlQuery sq = (SqlQuery) req;
			return sq.getSql();
		}
	}
	
	public String makeQueryCountSql(QueryRequest req) {
		
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			String whereStmt = tq.getWhere().toString();
			String sql = "select count(*) from "+tq.getTableName()+whereStmt;
			return sql;
		}
		else {
			SqlQuery sq = (SqlQuery)req;
			return "select count(*) from (" + sq.getSql() + ") tt";
		}
		
	}
	
	protected static boolean isNull(String s) {
		return s==null || s.trim().length()==0;
	}
	
	
}
