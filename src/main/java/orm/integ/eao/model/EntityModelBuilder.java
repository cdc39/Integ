package orm.integ.eao.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import orm.integ.dao.ColumnInfo;
import orm.integ.dao.DataAccessObject;
import orm.integ.eao.annotation.EntityAnno;
import orm.integ.eao.annotation.ForeignKey;
import orm.integ.utils.ClassAnalyzer;
import orm.integ.utils.IntegError;
import orm.integ.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class EntityModelBuilder implements EntityConfig {

	private EntityAnno entityAnno;

	private final Class<? extends Entity> entityClass;
	private String classId;
	private String tableName;
	private String tableKeyName;
	private String tableSchema;
	private String fullTableName;
	private String keyColumn;
	//private String nameColumn;
	private List<ColumnInfo> tableColumns ;
	private String[] normalFields ;
	private String[] defaultListFields ;
	private String[] defaultDetailFields;
	private Map<String, FieldInfo> fieldInfos = new HashMap<>();
	
	private Set<String> listExceptFields = new HashSet<>();
	private Set<String> detailExceptFields = new HashSet<>();
	
	private EntityModel model;
	
	@SuppressWarnings("unchecked")
	public EntityModelBuilder(Class entityClass, DataAccessObject dao) {
		
		this.entityClass = entityClass;
		entityAnno = (EntityAnno) entityClass.getAnnotation(EntityAnno.class);
		classId = entityAnno.classId();
		
		ClassAnalyzer ca = ClassAnalyzer.get(entityClass);
		normalFields = ca.getNormalFields();
		
		tableName = entityAnno.table();
		tableSchema = entityAnno.schema().trim();
		fullTableName = tableName;
		if (tableSchema.trim().length()>0) {
			fullTableName = tableSchema+"."+tableName;
		}
		
		tableKeyName = getTableKeyName();
		
		String keyColumn = tableKeyName+"_id";
		String nameColumn = tableKeyName+"_name";
		
		boolean exists = dao.tableExistTest(fullTableName);
		if (!exists) {
			throw new IntegError("table "+fullTableName+" not exists!");
		}
		this.tableColumns = dao.getTableColumns(fullTableName);  
		
		FieldInfo fi;
		Field f;
		String colName;
		ForeignKey fk;
		for (String field:normalFields) {
			fi = new FieldInfo(entityClass, field);
			f = ca.getField(field);
			fi.field = f;
			fk = f.getAnnotation(ForeignKey.class);
			if (fk!=null) {
				fi.masterClass = fk.masterClass();
			}
			fi.setter = ca.getSetterMethod(f);
			fi.getter = ca.getGetterMethod(field);
			fieldInfos.put(field, fi);

			colName = StringUtils.hump2underline(field);
			setFieldColumn(field, colName);
		}
		setFieldColumn("id", keyColumn);
		setFieldColumn("name", nameColumn);

	}
	
	private String getTableKeyName() {
		String tableKeyName = tableName;
		if (!isNull(entityAnno.shortName())) {
			tableKeyName = entityAnno.shortName().trim().toLowerCase();
		}
		else {
			int pos = tableName.indexOf("_");
			if (pos>=0&&pos<=3) {
				tableKeyName = tableName.substring(pos+1);
			}
		}
		return tableKeyName;
	}
	
	public EntityModel buildModel() {
		
		if (model==null) {
			model = new EntityModel();
		}
		model.entityAnno = entityAnno;
		model.classId = classId;
		model.entityClass = entityClass;
		model.tableName = tableName;
		model.tableKeyName = tableKeyName;
		model.tableSchema = tableSchema;
		model.fullTableName = fullTableName;
		model.keyColumn = keyColumn;
		model.tableColumns = tableColumns.toArray(new ColumnInfo[0]);
		
		List<String> fieldNames = getAllFieldName();
		
		int len = fieldNames.size();
		FieldInfo[] fields = new FieldInfo[len];
		for (int i=0; i<len; i++) {
			fields[i] = fieldInfos.get(fieldNames.get(i));
		}
		
		model.setFields(fields);
		
		model.defaultListFields = defaultListFields;
		if (defaultListFields==null) {
			model.defaultListFields = getExceptRestFields(listExceptFields);
		}
		
		model.defaultDetailFields = defaultDetailFields;
		if (defaultDetailFields==null) {
			model.defaultDetailFields = getExceptRestFields(detailExceptFields);
		}
		
		String colName;
		List<String> noFieldCols = new ArrayList<>();
		for (ColumnInfo col: tableColumns) {
			colName = col.getName().toLowerCase();
			if (!model.columnFieldMap.containsKey(colName)) {
				noFieldCols.add(colName);
			}
		}
		model.noFieldColumns = noFieldCols.toArray(new String[0]);
		
		EntityModels.putEntityModel(model);
		
		//model.print();
		
		return model;
	}
	
	private List<String> getAllFieldName() {
		List<String> fieldNames = new ArrayList<>();
		fieldNames.addAll(Arrays.asList(normalFields));
		for (FieldInfo field:fieldInfos.values()) {
			if (field.isMapping()) {
				fieldNames.add(field.getName());
			}
		}
		return fieldNames;
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
	public void setFieldColumn(String fieldName, String columnName) {
		FieldInfo fi = this.getField(fieldName);
		if (fi!=null) {
			fi.columnName = columnName;
			for (ColumnInfo col: tableColumns) {
				if (columnName.equalsIgnoreCase(col.getName())) {
					fi.column = col;
				}
			}
		}
		if (fieldName.equals("id")) {
			keyColumn = columnName;
		}
	}
	
	@Override
	public void setDefaultListFields(String... fields) {
		this.defaultListFields = formatViewFields(fields);
	}
	
	@Override
	public void setDefaultDetailFields(String... fields) {
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
			field = new FieldInfo(entityClass, fieldName);
			fieldInfos.put(fieldName, field);
		}
		return field;
	}
	
	private FieldInfo getField(String fieldName) {
		for (FieldInfo field: fieldInfos.values()) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return field;
			}
		}
		return null;
	}
	
	@Override
	public void addFieldMapping(String fieldName, String foreignKeyField, String relFieldName) {
		
		FieldInfo fkField = getField(foreignKeyField);
		
		String className = entityClass.getSimpleName();
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
	public void addNameMapping(String fieldName, String foreignKeyField) {
		addFieldMapping(fieldName, foreignKeyField, "name");
	}
	
	@Override
	public void addListFieldExcept(String... fields) {
		for (String f: fields) {
			listExceptFields.add(f);
		}
	}
	
	@Override
	public void addDetailFieldExcept(String... fields) {
		for (String f: fields) {
			detailExceptFields.add(f);
		}
	}


	private boolean isNull(String s) {
		return s==null||s.trim().equals("");
	}
	
}
