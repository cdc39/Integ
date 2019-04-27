package orm.integ.utils;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	@SuppressWarnings("rawtypes")
	public static <K> K create(Class<K> clazz, Map map) {
		if (clazz==null || map==null) {
			return null;
		}
		K obj = null;
		try {
			obj = clazz.newInstance();
			new ObjectHandler(obj).setValues(map);
			return  obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Record toRecord(Object obj) {
		Map<String, Object> values = new ObjectHandler(obj).getValues(false);
		Record rec = new Record();
		rec.putAll(values);
		return rec;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Record> toRecordList(List list) {
		List<Record> rtlist = new ArrayList<>();
		Record rec;
		for (Object obj: list) {
			if (obj!=null) {
				rec = toRecord(obj);
				rtlist.add(rec);
			}
		}
		return rtlist;
	}
	
	private Object object;
	private ClassModel cm;
	
	public ObjectHandler(Object object) {
		this.cm = ClassModel.get(object);
		this.object = object;
	}
	
	public Object getValue(String fieldName) {
		ClassField field = cm.getClassField(fieldName);
		if (field==null) {
			return null;
		}
		return field.getValue(object);
	}
	
	public void setValue(String fieldName, Object value)  
	{
		ClassField field = cm.getClassField(fieldName);
		if (field!=null) {
			field.setValue(object, value);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setValues(Map valueMap)  {
		Object value ;
		for (Object key: valueMap.keySet()) {
			value = valueMap.get(key);
			if (key!=null) {
				setValue(key.toString(), value);
			}
		}
	}
	
	public Map<String, Object> getValues(boolean includeNull) {
		return getValues(cm.getNormalFields(), includeNull);
	}
	
	public Map<String, Object> getValues(String[] fields, boolean includeNull) {
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
		Map<String, Object> values = obh.getValues(true);
		this.setValues(values);
	}

	public void copyValuesFrom(Object obj, String[] fields) {
		ObjectHandler obh = new ObjectHandler(obj);
		Map<String, Object> values = obh.getValues(fields, true);
		this.setValues(values);
	}
	
}
