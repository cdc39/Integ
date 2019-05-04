package orm.integ.eao.model;

import orm.integ.dao.ColumnInfo;
import orm.integ.utils.ClassField;

public class FieldInfo extends ClassField {
	
	public FieldInfo(Class<?> ownerClass, String name) {
		super(null);
		this.name = name;
		this.ownerClass = ownerClass;
	}
	
	public FieldInfo(Class<?> ownerClass, ClassField field) {
		super(field.getField());
		this.ownerClass = ownerClass;
		this.setter = field.getSetter();
		this.getter = field.getGetter();
	}
	
	final Class<?> ownerClass;
	
	ColumnInfo column;
	
	Class<? extends Entity> masterClass; 
	
	FieldMapping mapping;

	boolean isKey;
	
	public boolean isKey() {
		return isKey;
	}
	
	public String getColumnName() {
		return column==null?null:column.getName();
	}

	public ColumnInfo getColumn() {
		return column;
	}

	public boolean columnExists() {
		return column!=null;
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
