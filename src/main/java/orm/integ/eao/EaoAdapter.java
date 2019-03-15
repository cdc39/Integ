package orm.integ.eao;

import orm.integ.dao.DataAccessObject;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityConfig;
import orm.integ.eao.transaction.DataChange;
import orm.integ.utils.IdGenerator;

public abstract class EaoAdapter<T extends Entity> {

	protected abstract DataAccessObject getDao();
	
	protected void setEntityConfig(EntityConfig config) {} 
	
	protected void fillExtendFields(T entity) {}
	
	protected void beforeChange(DataChange change) {}
	
	protected void afterChange(DataChange change) {}
	
	public Object createNewId() {
		return IdGenerator.createRandomStr(12, false);
	}
	
}
