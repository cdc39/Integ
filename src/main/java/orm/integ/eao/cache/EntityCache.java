package orm.integ.eao.cache;

import orm.integ.eao.model.Entity;
import orm.integ.eao.transaction.ChangeTypes;
import orm.integ.eao.transaction.DataChange;
import orm.integ.eao.transaction.DataChangeListener;

public abstract class EntityCache<T extends Entity> implements DataChangeListener {

	public abstract T get(String id) ;
	
	public abstract void clear();
	
	public abstract void remove(String id);
	
	public abstract void put(T entity) ;

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(DataChange change) {
		try {
			if (change.getType()==ChangeTypes.INSERT) {
				this.put((T) change.getAfter());
			}
			else if (change.getType()==ChangeTypes.UPDATE) {
				this.put((T) change.getAfter());
			}
			else if (change.getType()==ChangeTypes.DELETE) {
				T entity = (T)change.getBefore();
				this.remove(entity.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
