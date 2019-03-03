package orm.integ.test;

import java.text.DecimalFormat;
import java.util.Date;

import orm.integ.utils.Convertor;

public class TimeMonitor {
	
	private static TimeMonitor lastMonitor;
	private static TimeMonitor allowedTimeMonitor ;

	private String task;
	private long beginTime = System.currentTimeMillis();
	private boolean finished = false;
	
	public TimeMonitor(String task) {
		this(task, false);
	}
	
	public TimeMonitor(String task, boolean locked) {
		this.task = task;
		if (locked && allowedTimeMonitor==null) {
			allowedTimeMonitor = this;
		}
		if (allowedTimeMonitor!=null && allowedTimeMonitor!=this) {
			return;
		}
		if (lastMonitor!=null && !lastMonitor.finished) {
			System.out.println();
		}
		String timeStr = Convertor.datetimeToStr(new Date());
		System.out.print("\n["+timeStr+"] "+task+" ... \n");
		lastMonitor = this;
	}
	
	public void setTaskName(String task) {
		this.task = task;
	}
	
	public void finish() {
		finish(null);
	}
	
	public void finish(String resultInfo) {
		if (allowedTimeMonitor!=null && allowedTimeMonitor!=this) {
			return;
		}
		if (resultInfo==null) {
			resultInfo = "";
		}
    	//long now = System.currentTimeMillis();
    	//long usedTime = now - beginTime;
    	String msg = resultInfo + " used " + getUsedTimeString(beginTime) ;
    	if (lastMonitor!=this) {
    		msg = "\n" + task + " finish, " + msg + "\n";
    	}
    	System.out.println(msg);
    	finished = true;
		if (allowedTimeMonitor==this) {
			allowedTimeMonitor = null;
		}
	}
	
	private String getUsedTimeString(long beginTime) {
		long usedTime = System.currentTimeMillis()-beginTime;
		if (usedTime<1000) {
			return new DecimalFormat("0.000").format(usedTime*1.0/1000) + " sec";
		}
		else if (usedTime<10000) {
			return new DecimalFormat("0.00").format(usedTime*1.0/1000) + " sec !!";
		}
		else if (usedTime<60000) {
			return new DecimalFormat("#0.0").format(usedTime*1.0/1000) + " sec !!!";
		}
		else 
		{
			int sec = (int) (usedTime/1000);
			int min = sec/60;
			int secRest = sec%60;
			return min + " min " + secRest + " sec !!!!";
		}
	}
	
}
