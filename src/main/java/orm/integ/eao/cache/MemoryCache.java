package orm.integ.eao.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import orm.integ.eao.model.Entity;

public class MemoryCache<T extends Entity> extends EntityCache<T> {

	protected final Map<Object, T> data = new ConcurrentHashMap<>();
	
	@Override
	public T get(String id) {
		if (id==null) {
			return null;
		}
		return data.get(id);
	}
	
	public T get(Object id) {
		if (id==null) {
			return null;
		}
		return data.get(id.toString());
	}
	
	@Override
	public void put(T entity) {
		if (entity==null) {
			return;
		}
		data.put(entity.getId(), entity);
	}
	
	@Override
	public void remove(String id) {
		if (data.get(id)!=null) {
			data.remove(id);
		}
	}
	
	@Override
	public void clear() {
		data.clear();
	}


}
