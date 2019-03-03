package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.List;

import orm.integ.eao.model.Entity;
import orm.integ.utils.FieldChange;
import orm.integ.utils.ObjectHandler;

public class DataChange {

	public static DataChange newUpdate(Entity before, Entity after) {
		DataChange dc = new DataChange();
		dc.type = ChangeTypes.UPDATE;
		dc.before = before;
		dc.after = after;
		dc.fieldChanges = ObjectHandler.findDifferents(before, after);
		return dc;
	}
	
	public static DataChange newInsert(Entity entity) {
		DataChange dc = new DataChange();
		dc.type = ChangeTypes.INSERT;
		dc.after = entity;
		return dc;
	}
	
	public static DataChange newDelete(Entity entity) {
		DataChange dc = new DataChange();
		dc.type = ChangeTypes.DELETE;
		dc.before = entity;
		return dc;
	}
	
	private int type;
	
	private Entity before;

	private Entity after;
	
	private List<FieldChange> fieldChanges;
	


	public int getType() {
		return type;
	}

	public void setType(int changeType) {
		this.type = changeType;
	}

	public Entity getBefore() {
		return before;
	}

	public void setBefore(Entity before) {
		this.before = before;
	}

	public Object getAfter() {
		return after;
	}

	public void setAfter(Entity after) {
		this.after = after;
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

}
