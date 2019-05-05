package orm.integ.eao.model;

import java.util.HashMap;
import java.util.Map;

public class FieldConfig {

	private static Map<String, Class<? extends Entity>> fkFields = new HashMap<>();
	
	public static void addForeignKey(String fieldName, Class<? extends Entity> clazz) {
		fkFields.put(fieldName, clazz);
	}
	
	public static Class<? extends Entity> getMasterClass(String fieldName) {
		return fkFields.get(fieldName);
	}
	
}
