package orm.integ.eao.transaction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.integ.utils.ClassModel;
import orm.integ.utils.IntegError;

public class TransactionManager {

	private static Map<Long, Transaction> transactions = new HashMap<>();

	public static Transaction getTran() {
		long threadId = getThreadId();
		return transactions.get(threadId);
	}
	
	public static void removeTran() {
		long threadId = getThreadId();
		transactions.remove(threadId) ;
	}

	public static void putTran(Transaction tran) {
		long threadId = getThreadId();
		transactions.put(threadId, tran);
	}
	
	public static long getThreadId() {
		return Thread.currentThread().getId();
	}

	public static void executeTransaction(Object service, String methodName, Object...values) {
		Method method = ClassModel.get(service).getMethod(methodName);
		Transaction tran = new EntityTransaction();
		putTran(tran);
		try {
			method.invoke(service, values);
			tran.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tran.rollback();
		}
		finally {
			tran.onFinally();
			removeTran();
		}
	}

	public static void afterChange(DataChange change, List<DataChangeListener> dataChangeListeners) {
		Transaction tran = TransactionManager.getTran();
		if (tran==null) {
			for (DataChangeListener ob: dataChangeListeners) {
				ob.notifyChange(change);
			}
			return;
		}
		change.dataChangeListeners = dataChangeListeners;
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
