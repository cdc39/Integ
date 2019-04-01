package orm.integ.eao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import orm.integ.eao.model.Entity;
import orm.integ.eao.model.Record;

@SuppressWarnings({"rawtypes","unchecked"})
public class Eaos {

	static Map<String, EntityAccessObject> eaos = new ConcurrentHashMap<>();

	public static Collection<EntityAccessObject> getAllEao() {
		return eaos.values();
	}
	
	public static void addEao(EntityAccessObject eao) {
		String className = eao.getEntityModel().getObjectClass().getName();
		eaos.put(className, eao);
	}
	
	public static EntityAccessObject getEao(Class clazz) {
		if (clazz==null) {
			return null;
		}
		return eaos.get(clazz.getName());
	}
	
	public static EntityAccessObject getEao(Entity entity) {
		if (entity==null) {
			return null;
		}
		else {
			return getEao(entity.getClass());
		}
		
	}
	
	public static <T> T getEntity(Class clazz, Object id) {
		EntityAccessObject eao = getEao(clazz);
		if (eao!=null) {
			return (T) eao.getById(id);
		}
		return null;
	}
	
	private static EntityAccessObject getEao(List<? extends Entity> list) {
		if (list.size()>0) {
			Class c = list.get(0).getClass();
			return getEao(c);
		}
		return null;
	}
	
	public static Record toRecord(Entity entity) {
		EntityAccessObject eao = getEao(entity);
		if (eao!=null) {
			return eao.toDetailRecord(entity);
		}
		return null;
	}
	
	public static Record toRecord(Entity entity, String[] fields) {
		EntityAccessObject eao = getEao(entity.getClass());
		if (eao!=null) {
			return eao.toRecord(entity, fields);
		}
		return null;
	}
	
	public static List<Record> toRecords(List<? extends Entity> list) {
		EntityAccessObject eao = getEao(list);
		if (eao!=null) {
			return eao.toRecords(list);
		}
		return new ArrayList<>();
	}
	
	public static List<Record> toRecords(List<? extends Entity> list, String[] fields) {
		EntityAccessObject eao = getEao(list); 
		if (eao!=null) {
			return eao.toRecords(list, fields);
		}
		return new ArrayList<>();
	}

	public static void insert(Entity entity) {
		EntityAccessObject eao = getEao(entity);
		if (eao!=null) {
			eao.insert(entity);
		}
	}
	
	public static void update(Entity entity) {
		EntityAccessObject eao = getEao(entity);
		if (eao!=null) {
			eao.update(entity);
		}
	}

}
