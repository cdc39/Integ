package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableModels {

	private static final Map<String, TableModel> models = new HashMap<>();
	private static final Map<String, TableModel> tabNameModels = new HashMap<>();
	
	private static Map<String, List<FieldInfo>> fkMap = new HashMap<>(); 
	
	public static void putModel(TableModel model) {
		if (model!=null) {
			models.put(model.getObjectClass().getName(), model);
			tabNameModels.put(model.getFullTableName().toLowerCase(), model);
		}
	}

	public static Collection<RelationModel> getRelationModels() {
		Set<RelationModel> relModels = new HashSet<>();
		for (TableModel tm: models.values()) {
			if (tm instanceof RelationModel) {
				relModels.add((RelationModel) tm);
			}
		}
		return relModels;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TableModel> T getModel(Class<?> clazz) {
		TableModel model = models.get(clazz.getName());
		if (model==null) {
			System.out.println(clazz.getName()+" 没有注册TableModel");
		}
		return  (T) model;
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
		for (TableModel em: models.values()) {
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

	@SuppressWarnings("unchecked")
	public static <T extends TableModel> T getByTableName(String tableName) {
		if (tableName==null) {
			return null;
		}
		return (T) tabNameModels.get(tableName.toLowerCase());
	}
	
}
