package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.List;

import orm.integ.eao.model.Entity;

public class DataChange {

	int type;
	
	Entity before;

	Entity after;
	
	@SuppressWarnings("rawtypes")
	List list;
	
	List<FieldChange> fieldChanges;
	
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

	public String[] getChangedFields() {
		List<String> fields = new ArrayList<>();
		for(FieldChange fc:fieldChanges) {
			fields.add(fc.getFieldName());
		}
		return fields.toArray(new String[0]);
	}
	
	public FieldChange[] getFieldChanges() {
		return fieldChanges.toArray(new FieldChange[0]);
	}

	public boolean containFields(String[] fields) {
		if (type==ChangeTypes.INSERT||type==ChangeTypes.DELETE) {
			return true;
		}
		if (fieldChanges!=null) {
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
