package orm.integ.eao;

import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.StatementAndValue;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.model.PageData;

public class EntitySqlQuery implements QueryHandler {

	@SuppressWarnings("rawtypes")
	EntityAccessObject eao ;
	DataAccessObject dao;
	EntityModel model;
	SqlQuery sqlQuery;
	
	@SuppressWarnings("rawtypes")
	EntitySqlQuery(EntityAccessObject eao, StatementAndValue sql) {
		this.eao = eao;
		dao = eao.getDAO();
		model = eao.getEntityModel();
		sqlQuery = new SqlQuery(sql);
	}

	public EntitySqlQuery setPageInfo(int start, int limit) {
		sqlQuery.setPageInfo(start, limit);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E extends Entity> E first() {
		return (E) eao.queryFirst(sqlQuery);
	}

	@Override
	public int count() {
		return eao.queryCount(sqlQuery);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List list() {
		return eao.query(sqlQuery);
	}
	
	@Override
	public PageData page() {
		return eao.pageQuery(sqlQuery);
	}
	
}
