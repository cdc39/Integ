package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityModels {

	private static Map<String, EntityModel> ems = new HashMap<>();
	private static Map<String, List<FieldInfo>> fkMap = new HashMap<>(); 
	
	static void putEntityModel(EntityModel em) {
		ems.put(em.getEntityClass().getName(), em);
	}
	
	public static List<FieldInfo> getForeignKeyFields(Class<? extends Entity> clazz) {
		List<FieldInfo> list = fkMap.get(clazz.getName());
		if (list==null) {
			list = scanForeignKeys(clazz);
			fkMap.put(clazz.getName(), list);
		}
		return list;
	}
	
	private static List<FieldInfo> scanForeignKeys(Class<? extends Entity> clazz) {
		List<FieldInfo> fields = new ArrayList<>();
		for (EntityModel em: ems.values()) {
			for (FieldInfo field: em.fields) {
				if (field.isForeignKey()) {
					if (field.getMasterClass()==clazz) {
						fields.add(field);
					}
				}
			}
		}
		return fields;
	}
	
}
