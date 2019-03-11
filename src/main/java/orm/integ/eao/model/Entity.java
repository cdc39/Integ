package orm.integ.eao.model;

import java.util.Date;

public class Entity implements HasId {

	protected String id;
	
	protected Date createTime; 

	public String getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id==null?null:id.toString();
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
