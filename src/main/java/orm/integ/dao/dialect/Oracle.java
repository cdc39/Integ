package orm.integ.dao.dialect;

import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.SqlBuilder;
import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.TabQuery;

public class Oracle extends SqlBuilder {

	@Override
	public String makePageQuerySql(QueryRequest req) {

		String orderStmt = req.getOrder().toString();
		String sql ;
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			String whereStmt = tq.getWhere().toString();
			sql = " select rownum rn, t.* from ("
				+ " select * from "+tq.getTableName()+whereStmt+orderStmt
				+ ") t where rownum<= "+ req.getLast();
		}
		else {
			SqlQuery sq = (SqlQuery)req;
			sql = "select rownum rn, t.* from (" + sq.getSql() + ") t where rownum<="+req.getLast();
		}
		if (req.getStart()>1) {
			sql = "select * from (" + sql + ") where rn>="+req.getStart();
		}
		return sql + orderStmt;
	}
	
	@Override
	public String getTestSql() {
		return "select * from dual";
	}
	
}
