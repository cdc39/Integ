package orm.integ.test.entity;

import orm.integ.dao.DataAccessObject;
import orm.integ.eao.EntityAccessService;
import orm.integ.eao.model.EntityConfig;
import orm.integ.test.DaoUtil;

public class SchoolClassService extends EntityAccessService<SchoolClass> {
	
	@Override
	public DataAccessObject getDao() {
		return DaoUtil.getDao();
	}
	
	@Override
	public void setEntityConfig(EntityConfig config) { 
	}

	@Override
	protected void fillExtendFields(SchoolClass entity) {
		
	}
	
}
