package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.List;

import orm.integ.eao.model.Entity;
import orm.integ.utils.ClassAnalyzer;
import orm.integ.utils.ObjectHandler;

public class ChangeFactory {

	public static DataChange newUpdate(Entity before, Entity after) {
		DataChange dc = new DataChange();
		dc.type = ChangeTypes.UPDATE;
		dc.before = (before);
		dc.after = after;
		//dc.fieldChanges = findDifferents(before, after);
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
	
	public static List<FieldChange> findDifferents(Object before, Object after) {
		List<FieldChange> changes = new ArrayList<>();
		if (before==null || after==null || before.getClass()!=after.getClass()) {
			return changes;
		}
		String[] fields = ClassAnalyzer.get(before).getNormalFields();
		Object v1, v2;
		ObjectHandler obh1 = new ObjectHandler(before);
		ObjectHandler obh2 = new ObjectHandler(after);
		FieldChange change;
		for (String field:fields) {
			v1 = obh1.getValue(field);
			v2 = obh2.getValue(field);
			if ((v1==null && v2!=null) || (v1!=null && !v1.equals(v2))) {
				change = new FieldChange();
				change.setFieldName(field);
				change.setBeforeValue(v1);
				change.setAfterValue(v2);
				changes.add(change);
			}
		}
		return changes;
	}

	@SuppressWarnings("rawtypes")
	public static DataChange newDeleteBatch(List list) {
		DataChange change = new DataChange();
		change.type = ChangeTypes.BATCH_DELETE;
		change.list = list;
		return change;
	}
	
}
