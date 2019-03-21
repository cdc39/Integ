package orm.integ.eao.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import orm.integ.dao.ColumnInfo;
import orm.integ.utils.Convertor;
import orm.integ.utils.MyLogger;

public class FieldInfo {
	
	public FieldInfo(Class<?> ownerClass, String name) {
		this.name = name;
		this.ownerClass = ownerClass;
	}
	
	final Class<?> ownerClass;
	
	final String name;
	
	String columnName;
	
	ColumnInfo column;
	
	Field field;
	
	Method setter ;
	
	Method getter ;

	Class<? extends Entity> masterClass; 
	
	FieldMapping mapping;
	
	public String getName() {
		return name;
	}

	public String getColumnName() {
		return columnName;
	}

	public ColumnInfo getColumn() {
		return column;
	}

	public boolean columnExists() {
		return column!=null;
	}
	
	public Field getField() {
		return field;
	}

	public Method getSetter() {
		return setter;
	}
	
	public Method getGetter() {
		return getter;
	}

	public Object getValue(Object entity) {
		if (entity!=null && getter!=null) {
			try {
				return getter.invoke(entity);
			}
			catch(Exception e) {
				throw new Error(e);
			}
		}
		return null;
	}
	
	public void setValue(Object obj, Object value) {
		if (setter==null) {
			return;
		}
		Object val = Convertor.translate(value, field.getType());
		try {
			setter.invoke(obj, val);
		}catch(Exception e) {
			MyLogger.printError(e, "field:"+name+", value="+value.getClass().getName()+":"+value);
		}
	}

	public boolean isNormal() {
		return getter!=null;
	}

	public boolean isForeignKey() {
		return masterClass!=null;
	}

	public Class<? extends Entity> getMasterClass() {
		return masterClass;
	}
	
	public Class<?> getOwnerClass() {
		return ownerClass;
	}
	
	public boolean isMapping() {
		return mapping!=null;
	}
	
	public FieldMapping getMapping() {
		return mapping;
	}
	
}
