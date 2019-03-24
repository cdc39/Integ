package orm.integ.eao.model;

import orm.integ.utils.IntegError;

public class RelationModel extends TableModel {

	String[] keyColumns;
	
	FieldInfo[] keyFields;
	
	String fieldPrefix;
	
	@Override
	public String[] getKeyColumns() {
		return keyColumns;
	}
	
	public String getFieldPrefix() {
		return fieldPrefix;
	}
	
	public FieldInfo[] getKeyFields(Class<? extends Entity> masterClass) {
		if (keyFields[0].getMasterClass()==masterClass) {
			return keyFields;
		}
		else if (keyFields[1].getMasterClass()==masterClass) {
			return new FieldInfo[]{keyFields[1], keyFields[0]};
		}
		throw new IntegError("输入的masterClass="+masterClass.getName()+"不匹配任何一个外键");
	}
	
	public FieldInfo[] getKeyFields() {
		return keyFields;
	}
	
	public FieldInfo getAnotherKeyField(Class<? extends Entity> masterClass) {
		if (keyFields[0].getMasterClass()==masterClass) {
			return keyFields[1];
		}
		else if (keyFields[1].getMasterClass()==masterClass) {
			return keyFields[0];
		}
		throw new IntegError("输入的masterClass="+masterClass.getName()+"不匹配任何一个外键");
	}
	
	public FieldInfo getKeyField(Class<? extends Entity> masterClass) {
		if (keyFields[0].getMasterClass()==masterClass) {
			return keyFields[0];
		}
		else if (keyFields[1].getMasterClass()==masterClass) {
			return keyFields[1];
		}
		throw new IntegError("输入的masterClass="+masterClass.getName()+"不匹配任何一个外键");
	}
	
}
