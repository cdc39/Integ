package orm.integ.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Convertor {
	
	@SuppressWarnings("rawtypes")
	public static Object translate(Object value, Class destClass) 
	{
		if (destClass==null)
		{
			return value;
		}
		String className = destClass.getName();
		if (value==null) {
			if (className.equals("int")||className.equals("long")
					||className.equals("double")) {
				return 0;
			}
			else {
				return null;
			}
		}
		if (value.getClass()==destClass) {
			return value;
		}
		if (className.equals("java.lang.String"))
		{
			value = toString(value);
		}
		else if (className.equals("int")) {
			value = toInt(value, 0);
		}
		else if (className.equals("long")) {
			value = toLong(value, 0);
		}
		else if (className.equals("double")) {
			value = toDouble(value, 0);
		}
		else if (className.equals("float")) {
			value = toFloat(value, 0);
		}
		else if (className.equals("java.lang.Integer"))
		{
			value = toInteger(value);
		}
		else if (className.equals("java.lang.Long")) {
			value = toLong(value);
		}
		else if (className.equals("java.lang.Double")) {
			value = toDouble(value);
 		}
		else if (className.equals("java.lang.Float")) {
			value = toFloat(value);
		}
		else if (className.equals("java.sql.Timestamp")) {
			if (value instanceof String) {
				Timestamp time = toTimestamp((String)value);
				return time;
			}
			else if ((value instanceof Date)) {
				Timestamp time = new Timestamp(((Date)value).getTime());
				return time;
			}
		}
		else if (className.equals("java.util.Date")) {
			if (value instanceof String) {
				Date date = strToDate((String)value);
				return date;
			}
		}
		return value;
	}
	
	public static int toInt(boolean value) {
		return value?1:0;
	}
	
	public static Integer toInteger(Object value) {
		if (value==null) {
			return null;
		}
		Integer intVal = null;
		if (value instanceof Integer) {
			intVal = (Integer)value;
		}
		else if (value instanceof String)
		{
			String str = (String) value;
			if (str.equals("")) {
				intVal = null;
			}
			else {
				if (str.indexOf('.')>0) {
					str = str.substring(0, str.indexOf('.'));
				}
				intVal = new Integer(str);
			}
		}
		else if (value instanceof Long) {
			Long longValue = (Long)value;
			intVal = longValue.intValue();
		}
		else if (value instanceof Double) {
			Double doubleValue = (Double)value;
			intVal = doubleValue.intValue();
		}
		else if (value instanceof Float) {
			Float floatValue = (Float)value;
			intVal = floatValue.intValue();
		}
		else if (value instanceof BigDecimal) {
			intVal = ((BigDecimal)value).intValue();
		}
		else if (value instanceof Short) {
			intVal = ((Short) value).intValue();
		}
		else if (value instanceof Boolean) {
			Boolean boolVal = (Boolean)value;
			intVal = boolVal.booleanValue()?1:0;
		}
		else {
			System.out.println("in toInteger, value class:"+value.getClass().getName());
		}
		return intVal;
	}
	
	public static int toInt(Object value, int defaultValue) {
		Integer v = toInteger(value);
		return v==null?defaultValue:v;
	}
	
	public static Long toLong(Object value) {
		Long longVal = null;
		if (value instanceof Long) {
			longVal = (Long)value;
		}
		if (value instanceof String )
		{
			if (!value.equals("")) {
				longVal = Long.parseLong((String)value);
			}
		}
		else if (value instanceof BigDecimal) {
			longVal = ((BigDecimal)value).longValue();
		}
		else if (value instanceof Integer) {
			longVal = ((Integer)value).longValue();
		}
		else if (value instanceof Short) {
			longVal = ((Short)value).longValue();
		}
		return longVal;
	}
	
	public static long toLong(Object value, long defaultValue) {
		Long v = toLong(value);
		return v==null?defaultValue:v;
	}
	
	public static Double toDouble(Object value) {
		Double d = null;
		if (value instanceof Double) {
			d = (Double)value;
		}
		else if (value instanceof String) {
			if (!value.equals("")) {
				d = new Double((String)value);
			}
		}
		else if (value instanceof Float) {
			Float f = (Float)value;
			d = f.doubleValue();
		}
		else if (value instanceof Integer) {
			d = new Double(value.toString());
		}
		else if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal)value;
			d = bd.doubleValue();
		}		
		return d;
	}
	
	public static double toDouble(Object value, double defaultValue) {
		Double d = toDouble(value);
		return d==null?defaultValue:d.doubleValue();
	}
	
	public static Float toFloat(Object value) {
		Float v = null;
		if (value instanceof Float) {
			v = (Float)value;
		}
		else if (value instanceof String) {
			v = new Float((String)value);
		}
		else if (value instanceof Double) {
			Double d = (Double)value;
			v = d.floatValue();
		}
		else if (value instanceof Integer) {
			v = new Float(value.toString());
		}
		else if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal)value;
			v = bd.floatValue();
		}
		return v;
	}
	
	public static float toFloat(Object value, float defaultValue) {
		Float v = toFloat(value);
		return v==null?defaultValue:v;
	}
	
	public static String toString(Object value) {
		
		if (value==null) {
			return null;
		}
		else if (value instanceof String) {
			return (String)value;
		}
		else if (value instanceof Date) {
			return datetimeToStr((Date)value);
		}
		else {
			return value.toString();
		}
		
	}

	public static Timestamp toTimestamp(String dateStr) {
		if (dateStr==null) {
			return null;
		}
		Date d = strToDate(dateStr);
		return d==null?null:new Timestamp(d.getTime());
	}
	
	public static Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	public static boolean toBool(String s) {
		if (s!=null) {
			s = s.trim().toLowerCase();
			if (s.equals("1") 
					|| s.equals("t") ||s.equals("true")
					|| s.equals("y") || s.equals("yes") 
					|| s.equals("on") ) {
				return true;
			}
		}
		return false;
	}
	
	public static String dateToStr(Date date) {
		return dateToStr(date, "yyyy-MM-dd");
	}
	
	public static String datetimeToStr(Date date) {
		return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String dateToStr(Date date, String format) {
		if (date==null) {
			return "";
		}
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		return fmt.format(date);
	}

	public static Date strToDate(String s) {
		if (s==null) {
			return null;
		}
		
		String formatStr = null;
		if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			formatStr = "yyyy-MM-dd";
		}
		else if (s.matches("\\d{8}")) {
			formatStr = "yyyyMMdd";
		}
		else if (s.matches("\\d{14}")) {
			formatStr = "yyyyMMddHHmmss";
		}
		else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}")) {
			formatStr = "yyyy-MM-dd HH:mm";
		}
		else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		
		Date d = null;
		if (formatStr!=null) {
			SimpleDateFormat fmt = new SimpleDateFormat(formatStr);
			try {
				d = fmt.parse(s);
			} catch (ParseException e) {
			}
		}
		return d;
		
	}
	
}
