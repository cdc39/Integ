package orm.integ.dao.dialect;

import orm.integ.dao.sql.OrderGroup;
import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.SqlBuilder;
import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.TabQuery;

public class SQLServer extends SqlBuilder {

	@Override
	public String makePageQuerySql(QueryRequest req) {

		String whereStmt, table;
		if (req instanceof TabQuery) {
			TabQuery tq = (TabQuery)req;
			whereStmt = tq.getWhere().toString();
			table = tq.getTableName();
		}
		else {
			SqlQuery sq = (SqlQuery)req;
			table = "("+sq.getSql()+") t ";
			whereStmt = " ";
		}
		
		String orderStmt = req.getOrder().toString();
		if (isNull(orderStmt)) {
			orderStmt = req.getKeyColumn();
		}
		OrderGroup orderGroup = OrderGroup.parse(orderStmt);
		
		orderStmt = orderGroup.toString();
		String revOrderStmt = orderGroup.toReverse().toString();
		
		if (req.getStart()<=1) {
			return "select top "+req.getLimit()+" * from "+table+whereStmt+orderStmt;
		}
		else {
			String columnsStr = orderGroup.getColumnsString();
			String keyColumn = req.getKeyColumn().toLowerCase();
			if (columnsStr.indexOf(keyColumn)<0) {
				columnsStr = keyColumn+", "+columnsStr;
			}
			return "select * from "+req.getTableName()+" where "+req.getKeyColumn()+" in ("
				+ " select top "+req.getLimit()+" "+columnsStr+" from ("
				+ " select top "+req.getLast()+" "+columnsStr
				+ " from "+table+whereStmt+orderStmt+") t1 "+revOrderStmt+") "+orderStmt;
		}
		
	}
	
	
	@Override
	public String getTestSql() {
		return "select 1";
	}
	
}
