package orm.integ.eao.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.QueryRequest;
import orm.integ.eao.model.Entity;
import orm.integ.eao.transaction.DataChange;
import orm.integ.eao.transaction.DataChangeListener;

public class QueryManager<T extends Entity> implements DataChangeListener {

	public static int idQueryCount = 0;
	public static int idQueryCacheHitCount = 0;
	
	public static int countQueryCount = 0;
	public static int countQueryCacheHitCount = 0;
	
	private DataAccessObject dao;
	
	public QueryManager(DataAccessObject dao) {
		this.dao = dao;
	}

	public final Map<Integer, IdQueryCache> idQueryCaches = new HashMap<>();
	
	public final Map<Integer, CountQueryCache> countQueryCaches = new HashMap<>();
	

	@Override
	public void notifyChange(DataChange change) {
		idQueryCaches.clear();
		countQueryCaches.clear();
	}

	public int queryCount(QueryRequest req) {
		
		if (!req.useCache()) {
			return dao.queryCount(req);
		}
		
		CountQueryCache cache = countQueryCaches.get(req.getCountQueryHashCode());
		if (cache==null) {
			cache = new CountQueryCache(req);
			cache.doQuery(dao);
			countQueryCount++;
			countQueryCaches.put(cache.hashCode(), cache);
		}
		else {
			cache.addOnce();
			countQueryCacheHitCount++;
		}
		return cache.getResult();
	}

	public List<String> queryIdList(QueryRequest req) {
		
		if (!req.useCache()) {
			return dao.queryIds(req);
		}
		
		QueryRequest cacheDataQuery = makeIdCacheQueryRequest(req);
		IdQueryCache cache = idQueryCaches.get(cacheDataQuery.hashCode());
		if (cache==null) {
			cache = new IdQueryCache(cacheDataQuery);
			cache.doQuery(dao);
			idQueryCount++;
			idQueryCaches.put(cache.hashCode(), cache);
		}
		else {
			cache.addOnce();
			idQueryCacheHitCount++;
		}
		List<String> ids = cache.getResult();
		ids = truncList(ids, req.getStart(), req.getLimit());
		return ids;
	}

	
	protected QueryRequest makeIdCacheQueryRequest(QueryRequest req) {
		
		int max, last = req.getLast();
		if (last<=100) {
			max = 100;
		}
		else if (last<=500) {
			max = 500;
		}
		else if (last<=2000){
			max = 2000;
		}
		else {
			max = 2000;
		}
		
		QueryRequest req2 = req.clone();
		req2.setPageInfo(1, max);
		
		return req2;
		
	}
	
	private List<String> truncList(List<String> list, int firstResult, int maxResults) {
		int toIndex = Math.min(list.size()-1,firstResult+maxResults-2);
		List<String> subList = new ArrayList<>();
		for (int i=firstResult-1; i<=toIndex; i++) {
			subList.add(list.get(i));
		}
		return subList;
	}

	
	public static void printStat() {
		System.out.println("idQueryCount="+idQueryCount+", idQueryCacheHitCount="+idQueryCacheHitCount);
		System.out.println("countQueryCount="+countQueryCount+", countQueryCacheHitCount="+countQueryCacheHitCount);
		
	}
	
}
