package orm.integ.eao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityConfig;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.transaction.ChangeTypes;
import orm.integ.eao.transaction.DataChange;
import orm.integ.eao.transaction.DataChangeListener;
import orm.integ.eao.transaction.Transaction;
import orm.integ.eao.transaction.TransactionManager;
import orm.integ.utils.ClassAnalyzer;
import orm.integ.utils.IntegError;

public abstract class EntityAccessService<T extends Entity> {

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
	


	protected abstract DataAccessObject getDao();
	
	protected abstract void setEntityConfig(EntityConfig config) ;
	
	protected abstract void fillExtendFields(T entity) ;
	
	protected void beforeDataChange(DataChange change) {};
	
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

	public void addDataChangeListener(DataChangeListener listener) {
		if (!dataChangeListeners.contains(listener)) {
			this.dataChangeListeners.add(listener);
		}
	}

	
	class EntityTransaction extends Transaction {
		
		@Override
		public List<DataChangeListener> getDataChangeListeners() {
			return dataChangeListeners;
		}
		@Override
		public void onBegin() {
			TransactionManager.putTran(this);
		}
		@Override
		public void onCommit() {
		}
		@SuppressWarnings("unchecked")
		@Override
		public void rollback(DataChange change) {
			if (change.getType()==ChangeTypes.INSERT) {
				T entity = (T)change.getAfter();
				eao.deleteById(entity.getId());
			}
			else if (change.getType()==ChangeTypes.DELETE) {
				T entity = (T)change.getAfter();
				eao.insert(entity);
			}
			else if (change.getType()==ChangeTypes.UPDATE) {
				T entity = (T) change.getBefore();
				eao.update(entity);
			}
		}
		
		@Override
		public void onFinally() {
			TransactionManager.removeTran();
		}
		
	}
	
	public void executeTransaction(String methodName, Object...values) {
		Method method = ClassAnalyzer.get(getClass()).getMethod(methodName);
		Transaction tran = new EntityTransaction();
		try {
			method.invoke(this, values);
			tran.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tran.rollback();
		}
	}
	
	void afterDataChange(DataChange change) {
		Transaction tran = TransactionManager.getTran();
		if (tran==null) {
			for (DataChangeListener ob:dataChangeListeners) {
				ob.notifyChange(change);
			}
			return;
		}
		if (tran.running()) {
			tran.addChange(change);
		}
		else if (tran.rollbacking()) {
		}
		else {
			throw new IntegError("事务状态异常");
		}
	}	

}
