package orm.integ.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class MyLogger {
	
	static Logger log;
	
	public static void printError(Throwable e, String... msgs) {
		List<String> lines = new ArrayList<>();
		StackTraceElement[] traces = e.getStackTrace();
		StackTraceElement[] traces2 = new Throwable().getStackTrace();
		String msg = msgs==null||msgs.length==0?null:msgs[0];
		if (msg!=null && msg.trim().length()!=0) {
			lines.add(msg);
		}
		lines.add(e.toString()+"\n    at "+traces[0].toString());
		lines.add(e.toString()+"\n    at "+traces[0].toString());
		lines.add("    at "+traces2[1].toString());
		lines.add("    at "+traces2[2].toString());
		print("Error", lines);
	}

	public static void print(String msg) {
		if (log==null) {
			log = Logger.getLogger(MyLogger.class);
		}
		log.info(msg);
	}

	@SuppressWarnings("rawtypes")
	public static void printMap(Map map, String name) {
		List<String> lines = new ArrayList<>();
		Object value;
		String line;
		for (Object key: map.keySet()) {
			value = map.get(key);
			if (value!=null) {
				line = "    "+key+" -> "+value+" ["+value.getClass().getSimpleName()+"]";
				lines.add(line);
			}
		}
		print(name, lines, "; ");
	}
	
	public static void print(String desc, List<String> lines) {
		print(desc, lines, "\n");
	}
	
	public static void print(String desc, List<String> lines, String delimiter) {
		String str = StringUtils.link(lines, delimiter);
		if (desc!=null) {
			str = desc + ": \n    " + str;
		}
		print(str);
	}	
	
}
