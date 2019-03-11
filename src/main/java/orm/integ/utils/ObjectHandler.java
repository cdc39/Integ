package orm.integ.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import orm.integ.eao.model.Record;


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
	
	private Object object;
	private ClassAnalyzer ca;
	
	public ObjectHandler(Object object) {
		this.ca = ClassAnalyzer.get(object);
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
		Object value;
		while (it.hasNext()) {
			name = (String)it.next();
			value = valueMap.get(name);
			setValue(name, value);
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
	
	public Record toRecord() {
		Record rec = new Record();
		Object value;
		String strVal;
		for (String fieldName:ca.getNormalFields()) {
			value = getValue(fieldName);
			if (value!=null) {
				strVal = Convertor.toString(value);
				rec.put(fieldName, strVal);
			}
		}
		return rec;
	}
	
	public Map<String, Object> getValueMap(boolean includeNull) {
		return getValueMap(ca.getNormalFields(), includeNull);
	}

	public Map<String, Object> getValueMap(String[] fields, boolean includeNull) {
		Map<String, Object> values = new HashMap<>();
		Object value;
		for (String fieldName:fields) {
			value = getValue(fieldName);
			if (includeNull || value!=null) {
				values.put(fieldName, value);
			}
		}
		return values;
	}

	public Object getObject() {
		return this.object;
	}
	
	public void copyValuesFrom(Object src) {
		ObjectHandler obh = new ObjectHandler(src);
		Map<String, Object> values = obh.getValueMap(true);
		this.setValues(values);
	}

	public void copyValuesFrom(Object obj, String[] fields) {
		ObjectHandler obh = new ObjectHandler(obj);
		Map<String, Object> values = obh.getValueMap(fields, true);
		this.setValues(values);
	}
	
}
