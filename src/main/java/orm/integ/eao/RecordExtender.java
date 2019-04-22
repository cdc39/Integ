package orm.integ.eao;

import orm.integ.eao.model.Entity;
import orm.integ.utils.Record;
import orm.integ.utils.StringUtils;

public abstract class RecordExtender {

	public abstract boolean fillRecordExt(Record record) ;
	
	public static void merge(Record record, String fkField, Class<? extends Entity> clazz, boolean override) {
		String fkId = record.getString(fkField);
		Entity entity = Eaos.getEntity(clazz, fkId);
		if (entity!=null) {
			Record rec2 = Eaos.toListRecord(entity);
			Object value;
			for (String field:rec2.keySet()) {
				value = rec2.get(field);
				if (override || !record.containsKey(field)) {
					record.put(field, value);
				}
			}
		}
	}
	
	public static void merge(Record record, String fkField, Class<? extends Entity> clazz) {
		merge(record, fkField, clazz, true);
	}
	
	public static void addSub(Record record, String fkField, String subField, Class<? extends Entity> clazz) {
		String fkId = record.getString(fkField);
		Entity entity = Eaos.getEntity(clazz, fkId);
		if (entity!=null) {
			Record rec2 = Eaos.toListRecord(entity);
			record.put(subField, rec2);
		}
	}
	
	public static void addSub(Record record, String fkField, Class<? extends Entity> clazz) {
		String subField = clazz.getSimpleName();
		subField = StringUtils.firstCharToLower(subField);
		addSub(record, fkField, subField, clazz);
		
	}
	
}
