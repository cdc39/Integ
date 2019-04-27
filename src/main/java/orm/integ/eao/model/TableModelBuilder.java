package orm.integ.eao.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.integ.dao.ColumnInfo;
import orm.integ.dao.DataAccessObject;
import orm.integ.dao.annotation.Column;
import orm.integ.dao.annotation.ForeignKey;
import orm.integ.dao.annotation.Table;
import orm.integ.utils.ClassField;
import orm.integ.utils.ClassModel;
import orm.integ.utils.IntegError;
import orm.integ.utils.StringUtils;

@SuppressWarnings("rawtypes")
public abstract class TableModelBuilder  {

	protected Table table;

	protected final Class<? extends Entity> objectClass;
	protected String tableName;
	protected String tableSchema;
	protected String fullTableName;
	protected List<ColumnInfo> tableColumns ;
	protected String[] normalFields ;
	
	protected Map<String, FieldInfo> fieldInfos = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public TableModelBuilder(Class objectClass, DataAccessObject dao) {
		
		this.objectClass = objectClass;
		table = (Table) objectClass.getAnnotation(Table.class);
		if (table==null) {
			throw new IntegError(objectClass.getSimpleName()+"未设置Table注解！");
		}
		
		ClassModel classModel = ClassModel.get(objectClass);
		normalFields = classModel.getNormalFields();
		
		tableName = table.name();
		tableSchema = table.schema().trim();
		fullTableName = tableName;
		if (tableSchema.trim().length()>0) {
			fullTableName = tableSchema+"."+tableName;
		}
		
		boolean exists = dao.tableExistTest(fullTableName);
		if (!exists) {
			throw new IntegError("table "+fullTableName+" not exists!");
		}
		this.tableColumns = dao.getTableColumns(fullTableName);  
		
		FieldInfo field;
		ClassField cf;
		Field f;
		String colName;
		ForeignKey fk;
		Column col;
		for (String fieldName:normalFields) {
			cf = classModel.getClassField(fieldName);
			f = cf.getField();
			
			field = new FieldInfo(objectClass, cf);
			
			col = f.getAnnotation(Column.class);
			if (col!=null) {
				colName = col.value();
			} else { 
				colName = StringUtils.hump2underline(fieldName);
			}
			setFieldColumn(field, colName);
			
			fk = f.getAnnotation(ForeignKey.class);
			if (fk!=null) {
				field.masterClass = fk.masterClass();
			}
			fieldInfos.put(fieldName, field);
		}

		setFieldColumn("createTime", table.createTimeColumn());

	}
	
	protected void setFieldColumn(String fieldName, String columnName) {
		setFieldColumn(getField(fieldName), columnName);
	}
	
	protected void setFieldColumn(FieldInfo field, String colName) {
		if (field==null || field.columnExists() || colName==null 
				|| colName.trim().length()==0) {
			return;
		}
		field.column = findColumn(colName);
	}	
	
	protected ColumnInfo findColumn(String colName) {
		for (ColumnInfo col: tableColumns) {
			if (colName.equalsIgnoreCase(col.getName())) {
				return col;
			}
		}
		return null;
	}
	
	protected void buildModel(TableModel model) {
		
		model.table = table;
		model.objectClass = objectClass;
		model.tableName = tableName;
		model.tableSchema = tableSchema;
		model.fullTableName = fullTableName;
		model.tableColumns = tableColumns.toArray(new ColumnInfo[0]);
		
		List<String> fieldNames = getAllFieldName();
		
		int len = fieldNames.size();
		FieldInfo[] fields = new FieldInfo[len];
		for (int i=0; i<len; i++) {
			fields[i] = fieldInfos.get(fieldNames.get(i));
		}
		
		model.setFields(fields);
		
		String colName;
		List<String> noFieldCols = new ArrayList<>();
		for (ColumnInfo col: tableColumns) {
			colName = col.getName().toLowerCase();
			if (!model.columnFieldMap.containsKey(colName)) {
				noFieldCols.add(colName);
			}
		}
	}
	
	protected List<String> getAllFieldName() {
		List<String> fieldNames = new ArrayList<>();
		fieldNames.addAll(Arrays.asList(normalFields));
		for (FieldInfo field:fieldInfos.values()) {
			if (field.isMapping()) {
				fieldNames.add(field.getName());
			}
		}
		return fieldNames;
	}
	
	FieldInfo getOrAddField(String fieldName) {
		FieldInfo field = fieldInfos.get(fieldName);
		if (field==null) {
			field = new FieldInfo(objectClass, fieldName);
			fieldInfos.put(fieldName, field);
		}
		return field;
	}
	
	protected FieldInfo getField(String fieldName) {
		for (FieldInfo field: fieldInfos.values()) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return field;
			}
		}
		return null;
	}
	

}
