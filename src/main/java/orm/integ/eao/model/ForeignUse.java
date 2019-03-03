package orm.integ.eao.model;

public class ForeignUse {

	private FieldInfo foreignKeyField;
	
	private int recordCount;

	public ForeignUse(FieldInfo fkField, int count) {
		this.foreignKeyField = fkField;
		this.recordCount = count;
	}

	public FieldInfo getForeignKeyField() {
		return foreignKeyField;
	}

	public int getRecordCount() {
		return recordCount;
	}

}
