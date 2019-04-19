package orm.integ.eao;

import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.StatementAndValue;

public class EntitySqlQuery extends QueryHandler {

	SqlQuery sqlQuery;
	
	@SuppressWarnings("rawtypes")
	EntitySqlQuery(EntityAccessObject eao, StatementAndValue sql) {
		super(eao);
		sqlQuery = new SqlQuery(sql);
		setQueryRequest(sqlQuery);
	}

}
