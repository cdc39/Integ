package orm.integ.eao;

import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.TabQuery;
import orm.integ.dao.sql.Where;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.model.PageData;

public class Query implements QueryHandler {

	@SuppressWarnings("rawtypes")
	EntityAccessObject eao ;
	DataAccessObject dao;
	EntityModel model;
	TabQuery tabQuery;
	
	public Query(Class<? extends Entity> clazz) {
		this(Eaos.getEao(clazz));
	}
	
	@SuppressWarnings("rawtypes")
	Query(EntityAccessObject eao) {
		this.eao = eao;
		dao = eao.getDAO();
		model = eao.getEntityModel();
		tabQuery = new TabQuery(model);
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
	
	public Query setPageInfo(int start, int limit) {
		tabQuery.setPageInfo(start, limit);
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

	@Override
	public PageData page() {
		return eao.pageQuery(tabQuery);
	}
	
}
