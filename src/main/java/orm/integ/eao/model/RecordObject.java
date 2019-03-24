package orm.integ.eao.model;

import java.util.Date;

public class RecordObject {

	protected Date createTime; 
	
	byte fromOrm = 0;
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
