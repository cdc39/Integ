package orm.integ.eao.transaction;

import java.util.ArrayList;
import java.util.List;

import orm.integ.eao.model.Entity;
import orm.integ.utils.ClassAnalyzer;
import orm.integ.utils.ObjectHandler;

public class ChangeFactory {

	public static DataChange newUpdate(Entity before, Entity after) {
		return new DataChange(ChangeTypes.UPDATE, before, after);
	}
	
	public static DataChange newInsert(Entity entity) {
		return new DataChange(ChangeTypes.INSERT, null, entity);
	}
	
	public static DataChange newDelete(Entity entity) {
		return new DataChange(ChangeTypes.DELETE, entity, null);
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

}
