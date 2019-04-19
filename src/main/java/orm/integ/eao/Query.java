package orm.integ.eao;

import java.util.List;

import orm.integ.dao.sql.TabQuery;
import orm.integ.dao.sql.Where;
import orm.integ.eao.model.Entity;

public class Query extends QueryHandler {

	TabQuery tabQuery;
	
	public Query(Class<? extends Entity> clazz) {
		this(Eaos.getEao(clazz));
	}
	
	@SuppressWarnings("rawtypes")
	Query(EntityAccessObject eao) {
		super(eao);
		tabQuery = new TabQuery(model);
		setQueryRequest(tabQuery);
	}

	public Query addWhere(String whereStmt, Object... values) {
		tabQuery.addWhereItem(whereStmt, values);
		return this;
	}
	
	public Query setWhere(Where where) {
		tabQuery.setWhere(where);
		return this;
	}
	
	public Query setOrder(String order) {
		tabQuery.setOrder(order);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E extends Entity> E first() {
		return (E) eao.queryFirst(tabQuery);
	}
	
	@Override
	public int count() {
		return eao.queryCount(tabQuery); 
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List list() {
		return eao.query(tabQuery);
	}

	
}
