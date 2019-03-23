package orm.integ.utils;

import java.util.Map;

public class MyLogger {

	public static void printError(Throwable e, String... msgs) {
		StackTraceElement[] traces = e.getStackTrace();
		StackTraceElement[] traces2 = new Throwable().getStackTrace();
		String msg = msgs==null||msgs.length==0?null:msgs[0];
		if (msg!=null && msg.trim().length()!=0) {
			System.out.println(msg);
		}
		System.out.println(e.toString()+"\n    at "+traces[0].toString());
		System.out.println("    at "+traces2[1].toString());
		System.out.println("    at "+traces2[2].toString());
		//e.printStackTrace();
	}

	public static void print(String msg) {
		System.out.println(msg);
	}

	@SuppressWarnings("rawtypes")
	public static void printMap(Map map, String name) {
		System.out.println(name+": ");
		Object value;
		for (Object key: map.keySet()) {
			value = map.get(key);
			if (value!=null) {
				System.out.println("    "+key+" -> "+value+" ["+value.getClass().getSimpleName()+"]");
			}
		}
	}
	
}
