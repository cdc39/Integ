package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.List;

import orm.integ.eao.model.Entity;

public class DataChange {

	int type;
	
	Entity before;

	Entity after;
	
	List<FieldChange> fieldChanges;
	
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
		else {
			return (E) before;
		}
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
		for (FieldChange fc: fieldChanges) {
			for (String field: fields) {
				if (fc.getFieldName().equals(field)) {
					return true;
				}
			}
		}
		return false;
	}

}
