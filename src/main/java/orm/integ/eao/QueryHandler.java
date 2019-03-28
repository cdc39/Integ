package orm.integ.eao;

import java.util.List;

import orm.integ.eao.model.Entity;
import orm.integ.eao.model.PageData;

public interface QueryHandler {

	public QueryHandler setPageInfo(int start, int limit);

	public <E extends Entity> E first() ;
	
	public int count() ;
	
	@SuppressWarnings("rawtypes")
	public List list() ;

	public PageData page();
	
}
