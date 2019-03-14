package orm.integ.eao.model;

public interface EntityConfig {

	public void setFieldColumn(String fieldName, String columnName);
	public void setFieldMapping(String fieldName, String foreignKeyField, String masterField);
	public void setNameMapping(String fieldName, String foreignKeyField);
	public void setListFields(String... fields) ;
	public void setDetailFields(String... fields) ;
	public void setDetailFieldExcept(String... fields);
	
}
