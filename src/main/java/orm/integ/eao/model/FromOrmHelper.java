package orm.integ.eao.model;

public class FromOrmHelper {

	public static boolean isFromOrm(RecordObject obj) {
		if (obj==null) {
			return false;
		}
		return obj.fromOrm==1;
	}
	
	public static void setFromOrm(RecordObject obj) {
		if (obj!=null) {
			obj.fromOrm = 1;
		}
	}
	
}
