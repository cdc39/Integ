package orm.integ.eao.transaction;

import orm.integ.eao.Eaos;
import orm.integ.eao.EntityAccessObject;
import orm.integ.eao.model.Entity;

public class EntityTransaction extends Transaction  {

	@Override
	public void onBegin() {
	}
	
	@Override
	public void onCommit() {
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void rollback(DataChange change) {
		EntityAccessObject eao = Eaos.getEao(change.getEntity());
		if (eao==null) {
			return;
		}
		Entity entity = change.getEntity();
		if (change.getType()==ChangeTypes.INSERT) {
			eao.deleteById(entity.getId(), false);
		}
		else if (change.getType()==ChangeTypes.DELETE) {
			eao.insert(entity);
		}
		else if (change.getType()==ChangeTypes.UPDATE) {
			eao.update(change.getBefore());
		}
	}
	
	@Override
	public void onFinally() {
		TransactionManager.removeTran();
	}
		
}
