package orm.integ.dao.dialect;

import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.SqlBuilder;

public class MySQL extends SqlBuilder {

	@Override
	public String makePageQuerySql(QueryRequest req) {
		String limitStmt = makePageLimitStmt(req);
		return makeQuerySql(req)+limitStmt;
	}

	private String makePageLimitStmt(QueryRequest req) {
		String stmt ;
		if (req.getStart()==0) {
			stmt = " limit "+req.getLimit();
		}
		else {
			stmt = " limit "+(req.getStart()-1)+","+req.getLimit();
		}
		return stmt;
	}

	@Override
	public String getTestSql() {
		return "select now()";
	}


	
}
