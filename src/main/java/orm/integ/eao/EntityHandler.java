package orm.integ.eao;

import orm.integ.eao.model.Entity;

public interface EntityHandler {

	@SuppressWarnings("rawtypes")
	public void handle(Entity entity, EntityAccessObject eao) ;
	
}
