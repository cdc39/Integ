package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import orm.integ.dao.DataAccessObject;
import orm.integ.utils.IntegError;

public class EntityModelBuilder extends TableModelBuilder implements EntityConfig {

	private String tableKeyName;
	private String[] defaultListFields ;
	private String[] defaultDetailFields;
	
	private Set<String> detailExceptFields = new HashSet<>();
	private Class<? extends Relation>[] relationClasses ;
	private DataAccessObject dao;
	
	public EntityModelBuilder(Class<? extends Entity> entityClass, DataAccessObject dao) {
		super(entityClass, dao);
		
		this.dao = dao;
		
		tableKeyName = getTableKeyName();
		
		setFieldColumn("id", table.keyColumn());
		setFieldColumn("id", tableKeyName+"_id");
		setFieldColumn("name", tableKeyName+"_name");
		
	}
	
	private String getTableKeyName() {
		String tableKeyName = tableName;
		int pos = tableName.indexOf("_");
		if (pos>=0&&pos<=3) {
			tableKeyName = tableName.substring(pos+1);
		}
		return tableKeyName;
	}
	
	public EntityModel buildModel() {
		
		EntityModel model = new EntityModel();
		super.buildModel(model);
		
		model.tableKeyName = tableKeyName;
		model.keyColumn = getField("id").getColumnName();
		
		List<String> fieldNames = getAllFieldName();
		
		model.listFields = defaultListFields;
		if (defaultListFields==null) {
			model.listFields = fieldNames.toArray(new String[0]);
		}
		
		model.detailFields = defaultDetailFields;
		if (defaultDetailFields==null) {
			model.detailFields = getExceptRestFields(detailExceptFields);
		}
		
		EntityModels.putEntityModel(model);
		
		if (relationClasses!=null) {
			for (Class<? extends Relation> rc: relationClasses) {
				new RelationModelBuilder(rc, dao).buildModel();
			}
		}
		
		return model;
	}
	
	private String[] getExceptRestFields(Collection<String> exceptFields) {
		List<String> fields = getAllFieldName();
		List<String> excepts = new ArrayList<>();
		String fieldName;
		for (String ef: exceptFields) {
			for (int i=0; i<fields.size(); i++) {
				fieldName = fields.get(i);
				if (fieldName.equalsIgnoreCase(ef)) {
					excepts.add(fieldName);
					break;
				}
			}
		}
		fields.removeAll(excepts);
		return fields.toArray(new String[0]);
	}

	@Override
	public void setListFields(String... fields) {
		this.defaultListFields = formatViewFields(fields);
	}
	
	@Override
	public void setDetailFields(String... fields) {
		this.defaultDetailFields = formatViewFields(fields);
	}
	
	// 格式化视图数据属性集，排除重复，固定顺序
	private String[] formatViewFields(String[] fieldNames) {
		if (fieldNames==null||fieldNames.length==0) {
			return fieldNames;
		}
		LinkedHashSet<String> fieldSet = new LinkedHashSet<>();
		List<String> allFields = this.getAllFieldName();
		for (String field1: allFields) {
			for (String field2: fieldNames) {
				if (field1.equalsIgnoreCase(field2)) {
					fieldSet.add(field1);
				}
			}
		}
		if (!fieldSet.contains("id")) {
			fieldSet.add("id");
		}
		return fieldSet.toArray(new String[0]);
	}
	
	FieldInfo getOrAddField(String fieldName) {
		FieldInfo field = fieldInfos.get(fieldName);
		if (field==null) {
			field = new FieldInfo(objectClass, fieldName);
			fieldInfos.put(fieldName, field);
		}
		return field;
	}
	
	@Override
	public void setFieldMapping(String fieldName, String foreignKeyField, String relFieldName) {
		
		FieldInfo fkField = getField(foreignKeyField);
		
		String className = objectClass.getSimpleName();
		String fkFieldName = className+"."+foreignKeyField;
		
		if (fkField==null) {
			throw new IntegError("外键属性 "+fkFieldName+"不存在！");
		}
		if (!fkField.isForeignKey()) {
			throw new IntegError(fkFieldName+"未设为外键！");
		}
		
		FieldMapping fm = new FieldMapping();
		fm.foreignKeyField = foreignKeyField;
		fm.masterField = relFieldName;
		
		FieldInfo field = this.getOrAddField(fieldName);
		field.mapping = fm;
		
	}

	@Override
	public void setNameMapping(String fieldName, String foreignKeyField) {
		setFieldMapping(fieldName, foreignKeyField, "name");
	}
	
	@Override
	public void setDetailFieldExcept(String... fields) {
		detailExceptFields.clear();
		for (String f: fields) {
			detailExceptFields.add(f);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setRelations(Class... classes) {
		this.relationClasses = classes;
	}

}
