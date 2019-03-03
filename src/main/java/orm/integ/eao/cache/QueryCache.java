package orm.integ.eao.cache;

import java.util.Date;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.QueryRequest;

public abstract class QueryCache<T> {

	public QueryCache(QueryRequest queryRt) {
		this.request = queryRt;
	}
	
	public abstract void doQuery(DataAccessObject dao) ;
	
	protected final QueryRequest request;
	
	private Date firstQueryTime;
	
	private Date lastQueryTime;
	
	private int queryCount = 0;

	protected T result;
	
	public void addOnce() {
		if (firstQueryTime==null) {
			firstQueryTime = new Date();
		}
		this.lastQueryTime = new Date();
		this.queryCount++;
	}
	
	public Date getFirstQueryTime() {
		return firstQueryTime;
	}

	public void setFirstQueryTime(Date firstQueryTime) {
		this.firstQueryTime = firstQueryTime;
	}

	public Date getLastQueryTime() {
		return lastQueryTime;
	}

	public void setLastQueryTime(Date lastQueryTime) {
		this.lastQueryTime = lastQueryTime;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
}
