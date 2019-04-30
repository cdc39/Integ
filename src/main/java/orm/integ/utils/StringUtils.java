package orm.integ.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringUtils {
	
	public static String repeat(String str, String separator, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<times; i++) {
			if (i>0) {
				sb.append(separator);
			}
			sb.append(str);
		}
		return sb.toString();
	}
	
	public static String repeat(String str, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public static String link(Object[] values, String delimiter) {
		if (values==null || delimiter==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		String value;
		for (int i=0; i<values.length; i++) {
			value = Convertor.toString(values[i]);
			if (i==0) {
				sb.append(value);
			}
			else {
				sb.append(delimiter).append(value);
			}
		}
		return sb.toString();
	}
	
	
	@SuppressWarnings("rawtypes")
	public static String link(Collection values, String delimiter) {
		if (values==null || delimiter==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Iterator it = values.iterator();
		while(it.hasNext()) {
			if (sb.length()==0) {
				sb.append(it.next());
			}
			else {
				sb.append(delimiter).append(it.next());
			}
		}
		return sb.toString();
	}
	
	public static String[] split(String line, String splitSign) {
		String[] values = line.split(splitSign);
		for (int i=0; i<values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}
	
	public static String hump2underline(String str) {
		int p1 = 0;
		boolean lower = false;
		char ch;
		String seg;
		List<String> segments = new ArrayList<String>();
		for (int i=0; i<str.length(); i++) {
			ch = str.charAt(i);
			if (lower&&ch>='A'&&ch<='Z') {
				lower = false;
				seg = str.substring(p1,i).toLowerCase();
				segments.add(seg);
				p1 = i;
			}
			else if (ch>='a'&&ch<='z') {
				lower = true;
			}
		}
		seg = str.substring(p1).toLowerCase();
		segments.add(seg);
		
		return link(segments, "_");
	}
	
	public static String underline2hump(String s) {
		if (s==null) {
			return null;
		}
		if (s.indexOf("_")<0) {
			return s;
		}
		s = s.toLowerCase();
		String[] parts = s.split("_");
		StringBuffer sb = new StringBuffer();
		for (String part: parts) {
			if (sb.length()==0) {
				sb.append(part);
			}
			else {
				if (part.length()>0) {
					String firstChar = part.substring(0, 1).toUpperCase();
					sb.append(firstChar).append(part.substring(1));
				}
			}
		}
		return sb.toString();
	}

	public static String removeBlank(String s) { 
		if (s==null) {
			return null;
		}
		s = s.replaceAll("( |\t|\n\r|\n|\r)", "");
		return s;
	}

	public static String getFirstWord(String s) {
		char c;
		for (int i=0; i<s.length(); i++) {
			c = s.charAt(i);
			if (c>='A' && c<='Z') {
				return s.substring(0,i);
			}
		}
		return s;
	}
	
	public static String firstCharToLower(String s) {
		if (s==null || s.length()==0) {
			return s;
		}
		String first = s.substring(0,1);
		return first.toLowerCase()+s.substring(1);
	}
	
	public static boolean inArray(String s, String[] array) {
		if (s==null) {
			return false;
		}
		boolean found = false;
		for (String item:array) {
			if (s.equalsIgnoreCase(item)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	
}
