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
import java.util.Set;

public class ClassModel {

	private static final Map<String, ClassModel> cms = 
			new HashMap<String, ClassModel>();
	
	public static ClassModel get(Object obj) {
		Class<?> c = obj instanceof Class?(Class<?>)obj:obj.getClass();
		ClassModel cm = cms.get(c.getName());
		if (cm==null) {
			cm = new ClassModel(c);
			cms.put(c.getName(), cm);
		}
		return cm;
	}
	
	public static Class<?>[] normalDataTypes = new Class[]{
			String.class, Integer.class, Long.class,  
			Double.class, Float.class, Short.class,
			Timestamp.class, Date.class,
			int.class, long.class, double.class, float.class
		};
	
	public static boolean isNormalDataType(Class<?> clazz) {
		for (Class<?> c: normalDataTypes) {
			if (clazz==c) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean fitMethod(Method method, Class<?>... paramTypes) {
		Class<?>[] methodParams = method.getParameterTypes();
		if (methodParams.length!=paramTypes.length) {
			return false;
		}
		Class<?> cp, cs;
		for (int i=0; i<methodParams.length; i++) {
			cp = methodParams[i];
			cs = paramTypes[i];
			if (!cp.isAssignableFrom(cs)) {
				return false;
			}
		}
		return true;
	}
	
	ClassModel(Class<?> c) {
		this.c = c;
		Class<?> cc = c;
		
		while (!cc.equals(Object.class)) {
			readFields(cc);
			readMethods(cc);
			cc = cc.getSuperclass();
		}
		
		normalFields = getFields(new FieldFilter(){
			@Override
			public boolean testField(Field field) {
				return isNormalDataType(field.getType());
			}
		});
		allFields = fieldList.toArray(new String[0]);
		
		ClassField cf;
		String fieldName;
		for (Field f:fieldList) {
			fieldName = f.getName();
			cf = new ClassField(f);
			cf.field = f;
			cf.setter = findSetterMethod(f);
			cf.getter = findGetterMethod(fieldName);
			classFieldMap.put(fieldName, cf);
		}
		
	}
	
	protected Class<?> c;
	String[] allFields;
	String[] normalFields;

	Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
	private List<Field> fieldList = new ArrayList<>();
	
	Map<String, List<Method>> methodMap = new HashMap<>();
	Map<String, ClassField> classFieldMap = new HashMap<>();
	
	
	private void readFields(Class<?> cc) {
		List<Field> list = new ArrayList<>();
		Field[] fields = cc.getDeclaredFields();
		int m;
		String fieldName;
		for (Field f: fields) {
			m = f.getModifiers();
			fieldName = f.getName();
			if (Modifier.isStatic(m)) {
				continue;
			}
			if (!fieldMap.containsKey(fieldName) 
					&& !fieldName.equals("fromOrm")) {
				list.add(f);
				fieldMap.put(fieldName, f);
			}
		}
		for (int i=0; i<list.size(); i++) {
			fieldList.add(i, list.get(i));
		}
	}
	
	private void readMethods(Class<?> cc) {
		Method[] ms = c.getMethods();
		List<Method> methodList;
		for (Method m: ms) {
			methodList = methodMap.get(m.getName());
			if (methodList==null) {
				methodList = new ArrayList<>();
				methodList.add(m);
				methodMap.put(m.getName(), methodList);
			}
			else {
				boolean foundSub = false;
				for (Method subMethod:methodList) {
					if (!fitMethod(m, subMethod.getParameterTypes())) {
						foundSub = true;
						break;
					}
				}
				if (!foundSub) {
					methodList.add(m);
				}
			}
		}
	}
	
	private Method findGetterMethod(String fieldName) {
		String methodName = getGetterName(fieldName);
		return getMethod(methodName);
	}
	
	private Method findSetterMethod(Field field) {
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
			Field field = this.fieldMap.get(fieldName);
			if (field.getType().getName().equals("boolean")) {
				return fieldName;
			}
		}
		String firstChar = String.valueOf(fieldName.charAt(0));
		fieldName = fieldName.replaceFirst(firstChar, firstChar.toUpperCase());
		return "get"+fieldName;
	}	
	
	public String[] getFields(FieldFilter filter) {
		List<String> fields = new ArrayList<String>();
		for(Field field: fieldList) {
			if (filter.testField(field)) {
				fields.add(field.getName());
			}
		}
		return fields.toArray(new String[0]);
	}

	public boolean fieldExists(String fieldName) {
		return fieldMap.get(fieldName)!=null;
	}
	
	public Method getGetterMethod(String fieldName) {
		ClassField cf = classFieldMap.get(fieldName);
		return cf==null?null:cf.getter;
	}
	
	public Method getSetterMethod(Field field) {
		ClassField cf = classFieldMap.get(field.getName());
		return cf==null?null:cf.setter;
	}
	
	
	public Method getMethod(String name, Class<?>... parameterTypes)  {
		Set<String> names = methodMap.keySet();
		for (String methodName: names) {
			if (methodName.equalsIgnoreCase(name)) {
				List<Method> methods = methodMap.get(methodName);
				for (Method m:methods) {
					if (fitMethod(m, parameterTypes)) {
						return m;
					}
				}
			}
		}
		return null;
	}

	public Field getField(String fieldName) {
		return this.fieldMap.get(fieldName);
	}
	
	public ClassField getClassField(String fieldName) {
		return this.classFieldMap.get(fieldName);
	}

	public String[] getNormalFields() {
		return normalFields;
	}
	
	public String[] getAllFields() {
		return allFields;
	}
	
}
