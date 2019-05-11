package orm.integ.eao;

import orm.integ.eao.model.Entity;

public interface EntityHandler<T extends Entity> {

	public void handle(T entity, EntityAccessObject<T> eao) ;
	
}
