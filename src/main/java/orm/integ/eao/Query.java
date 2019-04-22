package orm.integ.eao;

import java.util.ArrayList;
import java.util.List;

import orm.integ.dao.sql.TabQuery;
import orm.integ.dao.sql.Where;
import orm.integ.eao.model.Entity;
import orm.integ.utils.StringUtils;

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
	
	public Query where(String whereStmt, Object... values) {
		tabQuery.setWhere(new Where(whereStmt, values));
		return this;
	}
	
	public Query addWhere(boolean test, String whereStmt, Object... values) {
		if (test) {
			addWhere(whereStmt, values);
		}
		return this;
	}
	public Query addWhereNotNull(String whereStmt, Object value) {
		if (isNull(value)) {
			return addWhere(whereStmt, value);
		}
		else {
			return this;
		}
	}
	
	public Query addEqual(String colName, Object val) {
		if (isNull(val)) 
			return this;
		else 
			return this.addWhere(colName+"=?", val);
	}
	
	public Query addLike(String colName, String text) {
		if (!isNull(text)) {
			addWhere(colName + " like ?", "%"+text.trim()+"%");
		}
		return this;
	}
	
	public Query addLikes(String text, String... cols) {
		if (isNull(text)) {
			return this;
		}
		List<String> items = new ArrayList<>();
		Object[] values = new Object[cols.length];
		text = text.trim();
		for(int i=0; i<cols.length; i++) {
			items.add(cols[i] + " like ?");
			values[i] = "%"+text+"%";
		}
		String where = StringUtils.link(items, " or ");
		return addWhere(where, values);
	}	
	
	public Query addOrEquals(Object value, String... cols) {
		if (isNull(value)) {
			return this;
		}
		List<String> items = new ArrayList<>();
		Object[] values = new Object[cols.length];
		for(int i=0; i<cols.length; i++) {
			items.add(cols[i] + "=?");
			values[i] = value;
		}
		String where = StringUtils.link(items, " or ");
		return this.addWhere(where, values);
	}
	
	public Query setWhere(Where where) {
		tabQuery.setWhere(where);
		return this;
	}
	
	public Query setOrder(String order) {
		tabQuery.setOrder(order);
		return this;
	}
	
	public boolean isNull(Object v) {
		if (v==null) {
			return true;
		}
		if (v instanceof String) {
			String s = v.toString();
			if (s.trim().length()==0) {
				return true;
			}
		}
		return false;
	}
	
}
