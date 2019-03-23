package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableModels {

	private static final Map<String, TableModel> ems = new HashMap<>();
	//private static final Map<String, RelationModel> rms = new HashMap<>();
	
	private static Map<String, List<FieldInfo>> fkMap = new HashMap<>(); 
	
	public static void putModel(TableModel model) {
		if (model!=null) {
			ems.put(model.getObjectClass().getName(), model);
		}
	}

	public static Collection<RelationModel> getRelationModels() {
		Set<RelationModel> relModels = new HashSet<>();
		for (TableModel tm: ems.values()) {
			if (tm instanceof RelationModel) {
				relModels.add((RelationModel) tm);
			}
		}
		return relModels;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TableModel> T getByClass(Class<?> clazz) {
		return (T) ems.get(clazz.getName());
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
		for (TableModel em: ems.values()) {
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
