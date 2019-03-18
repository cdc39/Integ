package orm.integ.eao.model;

public class FromOrmHelper {

	public static boolean isFromOrm(Entity entity) {
		if (entity==null) {
			return false;
		}
		return entity.fromOrm==1;
	}
	
	public static void setFromOrm(Entity entity) {
		if (entity!=null) {
			entity.fromOrm = 1;
		}
	}
	
}
