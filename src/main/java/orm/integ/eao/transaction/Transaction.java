package orm.integ.eao.transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Transaction {

	public static final int STATE_WAITING = 0;
	public static final int STATE_BEGIN = 1;
	public static final int STATE_COMMITED = 2;
	public static final int STATE_ROLLBACKING = 3;
	public static final int STATE_ROLLBACKED = 4;
	
	public abstract void onBegin() ;
	
	public abstract void rollback(DataChange change) ;
	
	public abstract void onCommit() ;
	
	public abstract void onFinally() ;
	
	public Transaction() {
		state = STATE_BEGIN;
		beginTime = new Timestamp(System.currentTimeMillis());
		this.onBegin();
	}
	
	private final Timestamp beginTime;
	
	private int state;
	
	private final List<DataChange> changes = new ArrayList<>();

	public List<DataChange> getChanges() {
		return changes;
	}

	public void commit() {
		this.onCommit();
		this.state = STATE_COMMITED;
		List<DataChangeListener> listeners;
		for (DataChange change: changes) {
			listeners = change.dataChangeListeners;
			if (listeners!=null) {
				for (DataChangeListener ob: listeners) {
					ob.notifyChange(change);
				}
			}
		}
	}
	
	public int getState() {
		return state;
	}

	public void rollback() {
		this.state = STATE_ROLLBACKING;
		DataChange change;
		for (int i=changes.size()-1; i>=0; i--) {
			change = changes.get(i);
			rollback(change);
		}
		this.state = STATE_ROLLBACKED;
	}
	

	public boolean running() {
		return state == STATE_BEGIN;
	}
	public boolean rollbacking() {
		return state == STATE_ROLLBACKING;
	}
	
	public void addChange(DataChange change) {
		changes.add(change);
	}

	public Date getBeginTime() {
		return beginTime;
	}

}
