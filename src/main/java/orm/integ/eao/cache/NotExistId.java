package orm.integ.eao.cache;

public class NotExistId {

	NotExistId(String id) {
		this.id = id;
	}
	
	private String id;
	
	private long lastCheckTime;
	
	private int checkTimes = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public int getCheckTimes() {
		return checkTimes;
	}

	public void setCheckTimes(int checkTimes) {
		this.checkTimes = checkTimes;
	}

	public void addCheck() {
		lastCheckTime = System.currentTimeMillis();
		checkTimes++;
	}
	
	
	
}
