package orm.integ.utils;

public class FieldChange {

	private String fieldName;
	
	private Object beforeValue;
	
	private Object afterValue;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(Object beforeValue) {
		this.beforeValue = beforeValue;
	}

	public Object getAfterValue() {
		return afterValue;
	}

	public void setAfterValue(Object afterValue) {
		this.afterValue = afterValue;
	}
	
	
}
