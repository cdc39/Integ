package orm.integ.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassField {
	
	public ClassField(Field field) {
		this.field = field;
		if (field!=null) {
			this.name = field.getName();
		}
	}
	
	protected String name;
	
	protected Field field;
	
	protected Method setter ;
	
	protected Method getter ;

	public String getName() {
		return name;
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

	public Object getValue(Object obj) {
		if (obj!=null && getter!=null) {
			try {
				return getter.invoke(obj);
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

}
