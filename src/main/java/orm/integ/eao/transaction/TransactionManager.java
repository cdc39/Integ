package orm.integ.eao.transaction;

import java.util.HashMap;
import java.util.Map;

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
	
}
