package orm.integ.eao.model;

public interface EntityConfig {

	public void setFieldColumn(String fieldName, String columnName);
	public void addFieldMapping(String fieldName, String foreignKeyField, String masterField);
	public void addNameMapping(String fieldName, String foreignKeyField);
	public void setDefaultListFields(String... fields) ;
	public void setDefaultDetailFields(String... fields) ;
	public void addListFieldExcept(String... fields);
	public void addDetailFieldExcept(String... fields);
	
}
