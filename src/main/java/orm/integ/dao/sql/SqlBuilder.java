package orm.integ.dao.sql;

import java.util.ArrayList;
import java.util.List;

import orm.integ.utils.StringUtils;

public abstract class SqlBuilder {

	
	public static String formatWhereStmt(String whereStmt) {
		if (isNull(whereStmt)) {
			whereStmt = " ";
		}
		else {
			if (whereStmt.toLowerCase().trim().indexOf("where")!=0) {
				whereStmt = " where "+whereStmt;
			}
		}
		return whereStmt;
	}
	
	public static String formatWhereStmt(List<StatementAndValue> whereItems) {
		String stmt = toWhereStatement(whereItems);
		return formatWhereStmt(stmt);
	}
	
	public static String toWhereStatement(List<StatementAndValue> whereItems) {
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
