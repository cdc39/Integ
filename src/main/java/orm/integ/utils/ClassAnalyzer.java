package orm.integ.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ClassAnalyzer {
	
	private static final Map<String, ClassAnalyzer> cas = 
			new HashMap<String, ClassAnalyzer>();
	
	public static ClassAnalyzer get(Object obj) {
		Class c = obj instanceof Class?(Class)obj:obj.getClass();
		ClassAnalyzer ca = cas.get(c.getName());
		if (ca==null) {
			ca = new ClassAnalyzer(c);
			cas.put(c.getName(), ca);
		}
		return ca;
	}
	
	public static Class[] normalDataTypes = new Class[]{
			String.class, Integer.class, Long.class,  
			Double.class, Float.class, Short.class,
			Timestamp.class, Date.class,
			int.class, long.class, double.class, float.class
		};
	
	protected Class<?> c;
	private final String[] allFields;
	private final String[] normalFields;

	private Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
	private Map<Integer, Method> methods = new HashMap<>();
	
	public ClassAnalyzer(Class<?> c) {
		
		this.c = c;
		
		initFields();
		
		allFields = getFields(new FieldFilter(){
			@Override
			public boolean testField(Field field) {
				int m = field.getModifiers();
				return !Modifier.isStatic(m);
			}
		});
		
		normalFields = getFields(new FieldFilter(){
			@Override
			public boolean testField(Field field) {
				int m = field.getModifiers();
				if (field.getName().equals("fromOrm")) {
					return false;
				}
				return !Modifier.isStatic(m) && isNormalDataType(field.getType());
			}
		});
		Method[] ms = c.getMethods();
		int code;
		for (Method m: ms) {
			code = calcMethodCode(m.getName(), m.getParameterTypes());
			methods.put(code, m);
		}
	}
	
	private int calcMethodCode(String name, Class... paramTypes) {
		int code = name.hashCode();
		for (Class pt: paramTypes) {
			code = code*31+pt.hashCode();
		}
		return code;
	}

	private void initFields() {
		Class cc = c;
		Field[] fields;
		int m;
		while (!cc.equals(Object.class)) {
			fields = cc.getDeclaredFields();
			for (Field f: fields) {
				m = f.getModifiers();
				if (!Modifier.isStatic(m)) {
					if (fieldMap.get(f.getName())==null) {
						fieldMap.put(f.getName(), f);
					}
				}
			}
			cc = cc.getSuperclass();
		}
	}
	
	public Field getField(String fieldName) {
		if (fieldName==null) {
			return null;
		}
		return fieldMap.get(fieldName);
	}
	
	public Method getMethod(String name, Class... parameterTypes)  {
		Class cc = c;
		ClassAnalyzer ca;
		Method method = null; 
		int code;
		while(!cc.equals(Object.class) && method==null) {
			ca = ClassAnalyzer.get(cc);
			code = this.calcMethodCode(name, parameterTypes);
			method = ca.methods.get(code);
			cc = cc.getSuperclass();
		};
		return method;
	}
	
	public Method getGetterMethod(String fieldName) {
		String methodName = getGetterName(fieldName);
		return getMethod(methodName);
	}
	
	public Method getSetterMethod(Field field) {
		String methodName = getSetterName(field.getName());
		Method method = getMethod(methodName, field.getType());
		if (method==null) {
			method = getMethod(methodName, Object.class);
		}
		return method;
	}
	
	private String getSetterName(String fieldName)
	{
		String firstChar = String.valueOf(fieldName.charAt(0));
		fieldName = fieldName.replaceFirst(firstChar, firstChar.toUpperCase());
		return "set"+fieldName;
	}
	
	private String getGetterName(String fieldName)
	{
		String firstWord = StringUtils.getFirstWord(fieldName);
		if (firstWord.equals("is") || firstWord.equals("has")) {
			Field field = this.getField(fieldName);
			if (field.getType().getName().equals("boolean")) {
				return fieldName;
			}
		}
		String firstChar = String.valueOf(fieldName.charAt(0));
		fieldName = fieldName.replaceFirst(firstChar, firstChar.toUpperCase());
		return "get"+fieldName;
	}

	public boolean fieldExists(String fieldName) {
		return this.getField(fieldName)!=null;
	}
	
	public String[] getAllFields() {
		return this.allFields;
	}
	public String[] getNormalFields() {
		return this.normalFields;
	}
	
	public String[] getFields(FieldFilter filter) {
		List<String> fields = new ArrayList<String>();
		for(Field field: fieldMap.values()) {
			if (filter.testField(field)) {
				fields.add(field.getName());
			}
		}
		return fields.toArray(new String[0]);
	}
	
	public boolean isNormalDataType(Class clazz) {
		for (Class c: normalDataTypes) {
			if (clazz==c) {
				return true;
			}
		}
		return false;
	}

}


