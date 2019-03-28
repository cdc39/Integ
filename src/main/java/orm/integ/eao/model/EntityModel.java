package orm.integ.eao.model;

public class EntityModel extends TableModel {

	String tableKeyName;
	String keyColumn;
	String[] listFields;
	String[] detailFields;
	Class<? extends Relation>[] relationClasses;
	
	public String getKeyColumn() {
		return keyColumn;
	}
	
	@Override
	public String[] getKeyColumns() {
		return new String[]{keyColumn};
	}
	
	public String[] getListFields() {
		return notEmptyFields(listFields);
	}
	
	public String[] getDetailFields() {
		return notEmptyFields(detailFields);
	}
	
	private String[] notEmptyFields(String[] fields) {
		if (fields==null||fields.length==0) {
			return new String[]{"id", "name"};
		}
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Entity> getEntityClass() {
		return (Class<? extends Entity>) this.objectClass;
	}
	
	public Class<? extends Relation>[] getRelationClasses() {
		return this.relationClasses;
	}
	
}
