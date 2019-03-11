package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.integ.dao.ColumnInfo;
import orm.integ.dao.sql.TableInfo;
import orm.integ.eao.annotation.EntityAnno;
import orm.integ.utils.StringUtils;

public class EntityModel implements TableInfo {

	EntityAnno entityAnno;
	Class<? extends Entity> entityClass;
	String classId;
	String tableKeyName;
	String tableName;
	String tableSchema;
	String fullTableName;
	String keyColumn;
	ColumnInfo[] tableColumns ;
	String[] defaultListFields;
	String[] defaultDetailFields;
	String[] noFieldColumns;
	
	FieldInfo[] fields;
	Map<String, FieldInfo> fieldMap = new HashMap<>();
	Map<String, FieldInfo> columnFieldMap = new HashMap<>();
	
	void setFields(FieldInfo[] fields) {
		this.fields = fields;
		fieldMap.clear();
		columnFieldMap.clear();
		for (FieldInfo field:fields) {
			fieldMap.put(field.getName().toLowerCase(), field);
			if (field.columnExists()) {
				columnFieldMap.put(field.getColumnName().toLowerCase(), field);
			}
		}
	}
	
	public EntityAnno entityAnno() {
		return entityAnno;
	}
	
	public Class<? extends Entity> getEntityClass() {
		return entityClass;
	}
	
	public String getClassId() {
		return classId;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getTableSchema() {
		return tableSchema;
	}
	
	public String getFullTableName() { 
		return fullTableName;
	}
	
	public String getKeyColumn() {
		return keyColumn;
	}
	public ColumnInfo[] getColumns() {
		return tableColumns;
	}
	
	public boolean columnExists(String columnName) {
		if (columnName==null) {
			return false;
		}
		columnName = columnName.toLowerCase();
		return columnFieldMap.get(columnName)!=null;
	}
	
	public FieldInfo getFieldInfo(String fieldName) {
		if (fieldName==null) {
			return null;
		}
		FieldInfo field = fieldMap.get(fieldName.toLowerCase());
		if (field==null) {
			field = this.columnFieldMap.get(fieldName.toLowerCase());
		}
		return field;
	}

	public FieldInfo[] getFields() {
		return fields;
	}
	
	public String[] getFieldsExcept(String ...exceptFields) {
		List<String> restFields = new ArrayList<>();
		boolean found;
		for (FieldInfo field: fields) {
			found = false;
			for (String except: exceptFields) {
				if (field.getName().equalsIgnoreCase(except)) {
					found = true;
					break;
				}
			}
			if (!found) {
				restFields.add(field.getName());
			}
		}
		return restFields.toArray(new String[]{});
	}
	
	public String[] getDefaultListFields() {
		return notEmptyFields(defaultListFields);
	}
	
	public String[] getDefaultDetailFields() {
		return notEmptyFields(defaultDetailFields);
	}
	
	public String[] getNoFieldColumns() {
		return noFieldColumns;
	}
	
	private String[] notEmptyFields(String[] fields) {
		if (fields==null||fields.length==0) {
			return new String[]{"id", "name"};
		}
		return fields;
	}
	
	public FieldInfo getFieldByColumnName(String colName) {
		if (colName==null) {
			return null;
		}
		return columnFieldMap.get(colName.toLowerCase());
	}
	
	public void print() {
		System.out.println("\n载入实体模型："+entityClass.getSimpleName());
		System.out.println("对应数据库表："+this.fullTableName);
		System.out.printf("\n%10s%12s%16s\n", "属性名", "属性类别", "字段名");
		System.out.println("---------------------------------------------------------");
		String relColName, fieldType;
		for (FieldInfo field: fields) {
			relColName = field.column!=null?field.column.getName():"-";
			fieldType = field.isNormal()?"普通":"映射";
			System.out.printf("%16s %8s %22s\n", 
					field.getName(), fieldType, relColName);
		}
		String noFieldCols = StringUtils.link(noFieldColumns, ",");
		System.out.println("no field columns: "+noFieldCols);
	}
	
}
