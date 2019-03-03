package orm.integ.eao.cache;

import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.QueryRequest;

public class IdQueryCache extends QueryCache<List<String>> {

	private List<String> ids;

	public IdQueryCache(QueryRequest req) {
		super(req);
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	@Override
	public int hashCode() {
		return request.hashCode();
	}

	@Override
	public void doQuery(DataAccessObject dao) {
		this.result = dao.queryIds(request);
	}
	
}
