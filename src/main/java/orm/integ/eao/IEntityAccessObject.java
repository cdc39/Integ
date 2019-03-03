package orm.integ.eao;

import java.util.Collection;
import java.util.List;

import orm.integ.dao.sql.QueryRequest;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.PageData;
import orm.integ.eao.model.Record;

public interface IEntityAccessObject<T extends Entity> {

	T getById(Object id);
	
	@SuppressWarnings("rawtypes")
	List<T> getByIds(Collection ids);
	
	List<T> getAll(int maxReturn) ;
	
	void insert(T entity);
	
	void update(T entity, String[] fieldNames) ;
	
	void deleteById(Object id);

	//void deleteAll();
	
	
	List<T> query(QueryRequest queryReq) ;
	
	int queryCount(String whereStmt, Object... values);

	PageData pageQuery(QueryRequest req);
	
	Record toRecord(T entity, String[] fields) ;
	
	List<Record> toRecords(List<T> list, String[] fields) ;
	
}
