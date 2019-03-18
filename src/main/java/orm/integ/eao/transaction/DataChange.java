package orm.integ.eao.transaction;

import java.util.List;

import orm.integ.eao.model.Entity;

public class DataChange {

	int type;
	
	Entity before;

	Entity after;
	
	@SuppressWarnings("rawtypes")
	List list;
	
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
		if (after!=null) {
			return (E) after;
		}
		else if (before!=null){
			return (E) before;
		}
		else if (list!=null && list.size()>0) {
			return (E) list.get(0);
		}
		return null;
	}

	public FieldChange[] getFieldChanges() {
		if (fieldChanges==null) {
			List<FieldChange> diffes = ChangeFactory.findDifferents(before, after);
			fieldChanges = diffes.toArray(new FieldChange[0]);
		}
		return fieldChanges;
	}

	public boolean containFields(String... fields) {
		if (type==ChangeTypes.INSERT||type==ChangeTypes.DELETE) {
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

	@SuppressWarnings("rawtypes")
	public List getList() {
		return list;
	}

}
