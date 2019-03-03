package orm.integ.eao.cache;

import java.util.HashMap;
import java.util.Map;

import orm.integ.eao.model.Entity;
import orm.integ.eao.transaction.ChangeTypes;
import orm.integ.eao.transaction.DataChange;
import orm.integ.eao.transaction.DataChangeListener;

public class NoExistsCache implements DataChangeListener {

	private Map<String, NotExistId> notExistIds = new HashMap<>();
	
	public void signNotExist(Object id) {
		if (id==null) {
			return;
		}
		NotExistId neid = notExistIds.get(id.toString());
		if (neid==null) {
			neid = new NotExistId(id.toString());
			notExistIds.put(id.toString(), neid);
		}
		neid.addCheck();
	}
	
	public boolean isNotExistId(Object id) {
		NotExistId neid = notExistIds.get(id.toString());
		if (neid==null) {
			return false;
		}
		long timeDis = System.currentTimeMillis()-neid.getLastCheckTime();
		if (timeDis>60000) {
			return false;
		}
		return neid.getCheckTimes()>1;
	}
	
	public void removeNotExist(Object id) {
		notExistIds.remove(id.toString());
	}

	@Override
	public void notifyChange(DataChange change) {
		if (change.getType()==ChangeTypes.INSERT) {
			Entity en = change.getEntity();
			notExistIds.remove(en.getId());
		}
	}
	
}
