package orm.integ.dao.dialect;

import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.SqlBuilder;
import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.TabQuery;

public class PostgreSQL extends SqlBuilder {

	@Override
	public String makePageQuerySql(QueryRequest req) {

		String limitStmt = makePageLimitStmt(req);
		String orderStmt = req.getOrder().toString();
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			String whereStmt = tq.getWhere().toString();
			return "select * from "+req.getTableName()+whereStmt+orderStmt+limitStmt;
		}
		else {
			SqlQuery sq = (SqlQuery)req;
			String sql = sq.getSql();
			return "select * from ("+sql+") t "+orderStmt+limitStmt;
		}
		
	}
	
	private String makePageLimitStmt(QueryRequest req) {
		String stmt ;
		if (req.getStart()<=1) {
			stmt = " limit "+req.getLimit();
		}
		else {
			stmt = " limit "+req.getLimit()+" offset "+req.getStart();
		}
		return stmt;
	}
	
	
	@Override
	public String getTestSql() {
		return "select now()";
	}
	
}
