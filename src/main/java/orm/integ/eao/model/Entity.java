package orm.integ.eao.model;

import java.util.Date;

public class Entity {

	protected String id;
	
	protected String name;
	
	protected Date createTime;
	

	public String getId() {
		return id;
	}
	
	public int getIntId() {
		return Integer.parseInt(id);
	}

	public void setId(Object id) {
		this.id = id==null?null:id.toString();
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) { 
		this.name = name;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	protected boolean isNull(String s) {
		return s==null||s.trim().length()==0;
	}
	
}
