package orm.integ.eao.model;

public class Entity extends RecordObject implements HasId {

	protected String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id==null?null:id.toString();
	}
	
}
