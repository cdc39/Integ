package orm.integ.eao.cache;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.QueryRequest;

public class CountQueryCache extends QueryCache<Integer> {

	private int count;

	
	public CountQueryCache(QueryRequest queryRt) {
		super(queryRt);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int hashCode() {
		return request.getCountQueryHashCode();
	}
	
	@Override
	public void doQuery(DataAccessObject dao) {
		this.result = dao.queryCount(request);
	}
	
}
