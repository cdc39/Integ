package orm.integ.eao;

import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.StatementAndValue;
import orm.integ.eao.model.Entity;

public class EntitySqlQuery extends QueryHandler {

	SqlQuery sqlQuery;
	
	public EntitySqlQuery(Class<? extends Entity> entityClass, StatementAndValue sql) {
		super(Eaos.getEao(entityClass));
	}
	
	@SuppressWarnings("rawtypes")
	EntitySqlQuery(EntityAccessObject eao, StatementAndValue sql) {
		super(eao);
		sqlQuery = new SqlQuery(sql);
		setQueryRequest(sqlQuery);
	}

}
