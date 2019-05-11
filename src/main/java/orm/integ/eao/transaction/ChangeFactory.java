package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import orm.integ.eao.model.Entity;
import orm.integ.utils.ClassModel;
import orm.integ.utils.ObjectHandler;

public class ChangeFactory {

	public static DataChange newUpdate(Entity before, Entity after) {
		return new DataChange(DataChange.UPDATE, before, after);
	}
	
	public static DataChange newInsert(Entity entity) {
		return new DataChange(DataChange.INSERT, null, entity);
	}
	
	public static DataChange newDelete(Entity entity) {
		return new DataChange(DataChange.DELETE, entity, null);
	}
	
	public static List<FieldChange> findDifferents(Object before, Object after) {
		List<FieldChange> changes = new ArrayList<>();
		if (before==null || after==null || before.getClass()!=after.getClass()) {
			return changes;
		}
		ClassModel ca = ClassModel.get(before);
		String[] fields = ca.getNormalFields();
		
		Object v1, v2;
		ObjectHandler obh1 = new ObjectHandler(before);
		ObjectHandler obh2 = new ObjectHandler(after);
		FieldChange change;
		for (String field:fields) {
			v1 = obh1.getValue(field);
			v2 = obh2.getValue(field);
			if (!equals(v1, v2)) {
				change = new FieldChange();
				change.setFieldName(field);
				change.setBeforeValue(v1);
				change.setAfterValue(v2);
				changes.add(change);
			}
		}
		return changes;
	}

	public static boolean equals(Object v1, Object v2) {
		if (v1==null) {
			if (v2==null) {
				return true;
			}
			else {
				return false;
			}
		}
		if (v2==null) {
			return false;
		}
		if (v1==v2) {
			return true;
		}
		if (v1 instanceof Date) {
			Date d1 = (Date)v1;
			Date d2 = (Date)v2;
			int dis = (int) (d1.getTime()/1000 - d2.getTime()/1000);
			return dis==0;
		}
		if (v1.getClass()!=v2.getClass()) {
			return false;
		}
		return v1.equals(v2);
	}
	
}
