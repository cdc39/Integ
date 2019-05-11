package orm.integ.eao.transaction;

import java.util.List;

import orm.integ.eao.model.Entity;

public class DataChange {

	public static final int INSERT = 1;
	public static final int DELETE = 2;
	public static final int UPDATE = 3;
	
	public DataChange(int changeType, Entity before, Entity after) {
		this.type = changeType;
		this.before = before;
		this.after = after;
		this.entity = after!=null?after:before;
		this.entityClass = entity.getClass();
	}

	final int type;
	final Entity before;
	final Entity after;
	final Entity entity;
	final Class<? extends Entity> entityClass;
	
	private FieldChange[] fieldChanges;
	
	List<DataChangeListener> dataChangeListeners;
	
	public int getType() {
		return type;
	}

	public Entity getBefore() {
		return before;
	}

	public Object getAfter() {
		return after;
	}

	@SuppressWarnings("unchecked")
	public <E extends Entity> E getEntity() {
		return (E) entity;
	}
	
	public Class<? extends Entity> getEntityClass() {
		return entityClass;
	}

	public FieldChange[] getFieldChanges() {
		if (fieldChanges==null) {
			List<FieldChange> diffes = ChangeFactory.findDifferents(before, after);
			fieldChanges = diffes.toArray(new FieldChange[0]);
		}
		return fieldChanges;
	}
	
	public FieldChange getFieldChange(String fieldName) {
		FieldChange[] fcs = getFieldChanges();
		for (FieldChange fc: fcs) {
			if (fc.getFieldName().equals(fieldName)) {
				return fc;
			}
		}
		return null;
	}

	public boolean containFields(String... fields) {
		if (type==INSERT||type==DELETE) {
			return true;
		}
		if (fields==null || fields.length==0) {
			return false;
		}
		if (getFieldChanges()!=null) {
			for (FieldChange fc: fieldChanges) {
				for (String field: fields) {
					if (fc.getFieldName().equals(field)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
