package orm.integ.eao.model;

import orm.integ.dao.annotation.Key;

public class Entity extends RecordObject implements HasId {

	@Key
	protected String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id==null?null:id.toString();
	}
	
}
