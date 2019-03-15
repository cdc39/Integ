package orm.integ.eao;

import java.util.ArrayList;
import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.transaction.DataChangeListener;
import orm.integ.eao.transaction.TransactionManager;
import orm.integ.utils.IdGenerator;

public abstract class EntityAccessService<T extends Entity> extends EaoAdapter<T> {

	protected EntityAccessObject<T> eao;
	protected DataAccessObject dao;
	protected EntityModel em;
	protected final List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	public EntityAccessService() {
		eao = new EntityAccessObject<T>(this);
		dao = this.getDao();
		this.em = eao.getEntityModel();
		
		if (this instanceof DataChangeListener) {
			dataChangeListeners.add((DataChangeListener) this);
		}
	}
	
	public EntityAccessObject<T> getEao() {
		return eao;
	}

	public EntityModel getEntityModel() {
		return em;
	}
	
	public Object createNewId() {
		int keyLength = em.entityAnno().keyLength();
		return IdGenerator.createRandomStr(keyLength, false);
	}

	public void executeTransaction(String methodName, Object...values) {
		TransactionManager.executeTransaction(this, methodName, values);
	}
	
}
