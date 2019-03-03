package orm.integ.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ObjectHandler {
	
	@SuppressWarnings("unchecked")
	public static <X> X clone(X object) {
		if (object==null) {
			return null;
		}
		try {
			X newObj = (X) object.getClass().newInstance();
			new ObjectHandler(newObj).copyValuesFrom(object);
			return newObj;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void merge(Object src, Object dest) throws Exception {
		List<FieldChange> diffs = findDifferents(dest, src);
		ObjectHandler obh = new ObjectHandler(dest);
		for (FieldChange ch: diffs) {
			obh.setValue(ch.getFieldName(), ch.getAfterValue());
		}
	}
	
	public static List<FieldChange> findDifferents(Object before, Object after) {
		List<FieldChange> changes = new ArrayList<>();
		if (before.getClass()!=after.getClass()) {
			return changes;
		}
		Map<String, Object> values;
		try {
			values = new ObjectHandler(before).getValueMap();
			Object v1, v2;
			ObjectHandler obh2 = new ObjectHandler(after);
			FieldChange change;
			for (String field:values.keySet()) {
				v1 = values.get(field);
				v2 = obh2.getValue(field);
				if ((v1==null && v2!=null) || !v1.equals(v2)) {
					change = new FieldChange();
					change.setFieldName(field);
					change.setBeforeValue(v1);
					change.setAfterValue(v2);
					changes.add(change);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changes;
	}
	
	private Object object;
	@SuppressWarnings("rawtypes")
	protected Class objectClass;
	private ClassAnalyzer ca;
	
	public ObjectHandler(Object object) {
		this.objectClass = object.getClass();
		this.ca = ClassAnalyzer.get(objectClass);
		this.object = object;
	}
	
	public String getStringValue(String fieldName) {
		Object value = getValue(fieldName) ;
		return value==null?null:Convertor.toString(value); 
	}
	
	public String getStringValue(Field f) throws Exception {
		Object value = getValue(f) ;
		return value==null?null:value.toString(); 
	}
	
	
	public Object getValue(String fieldName) {
		Field field = ca.getField(fieldName);
		if (field==null) {
			return null;
		}
		try {
			Method getter = ca.getGetterMethod(fieldName);
			if (getter!=null) {
				return getter.invoke(object);
			}
			if (field.isAccessible()) {
				return field.get(object);
			}
		}
		catch(Exception e) {
			MyLogger.printError(e);
		}
		return null;
	}
	
	public void setValue(String fieldName, Object value)  
	{
		Field field = ca.getField(fieldName);
		if (field!=null) {
			this.setValue(field, value);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setValues(Map valueMap)  {
		Iterator it = valueMap.keySet().iterator();
		String name;
		while (it.hasNext()) {
			name = (String)it.next();
			setValue(name, valueMap.get(name));
		}
	}
	
	public Object getValue(Field field)  
	{
		Method getter = ca.getGetterMethod(field.getName());
		Object value = null;
		try {
			if(getter!=null)
			{
				value = getter.invoke(object);
			}
			else if (field.isAccessible())
			{
				value = field.get(object);
			}
		}
		catch(Exception e) {
			MyLogger.printError(e);
			MyLogger.print("field="+field);
		}
		return value;
	}
	
	public void setValue(Field field, Object value) 
	{
		Method setter = ca.getSetterMethod(field);
		try
		{
			value = Convertor.translate(value, field.getType());
			if(setter!=null)
			{
				setter.invoke(object, new Object[]{value});
			}
			else if (field.isAccessible())
			{
				field.set(object, value);
			}
		}
		catch(Exception e) {
			MyLogger.printError(e);
			String msg = "set field value failed, field:"+field.getName()+", value="+value
					+", value class = "+value.getClass().getName()
					+", setter="+setter+", field.type="+field.getType().getName();
			MyLogger.print(msg);
		}
	}
	
	public Map<String, Object> getValueMap() {
		return getValueMap(ca.getNormalFields());
	}

	public Map<String, Object> getValueMap(String[] fields) {
		Map<String, Object> values = new HashMap<>();
		Object value;
		for (String fieldName:fields) {
			value = getValue(fieldName);
			if (value!=null) {
				values.put(fieldName, value);
			}
		}
		return values;
	}

	public Object getObject() {
		return this.object;
	}
	
	public ClassAnalyzer getClassAnalyzer() {
		return this.ca;
	}

	public void copyValuesFrom(Object src) {
		ObjectHandler obh = new ObjectHandler(src);
		Map<String, Object> values = obh.getValueMap();
		this.setValues(values);
	}
	
}
