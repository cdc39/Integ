package orm.integ.eao.model;

import orm.integ.utils.IntegError;

public class RelationModel extends TableModel {

	String[] keyColumns;
	FieldInfo[] keyFields;
	
	@Override
	public String[] getKeyColumns() {
		return keyColumns;
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Relation> getRelationClass() {
		return (Class<? extends Relation>) this.objectClass;
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
	
	
}
