package orm.integ.eao.cache;

import orm.integ.eao.model.Entity;

public interface EntityDockListener<T extends Entity> {

	public void onEntityDock(T entity);
	
}
