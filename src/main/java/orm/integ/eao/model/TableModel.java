package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.integ.dao.ColumnInfo;
import orm.integ.dao.annotation.Table;
import orm.integ.dao.sql.TableInfo;
import orm.integ.utils.Convertor;
import orm.integ.utils.StringUtils;

public class TableModel implements TableInfo {

	protected Class<?> objectClass;
	Table table;
	String tableName;
	String tableSchema;
	String fullTableName;
	ColumnInfo[] tableColumns ;
	String[] keyColumns;

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
	
	@SuppressWarnings("rawtypes")
	public Class getObjectClass() {
		return objectClass;
	}
	
	public Table getTable() {
		return table;
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	public String getTableSchema() {
		return tableSchema;
	}
	
	@Override
	public String getFullTableName() { 
		return fullTableName;
	}
	
	@Override
	public String[] getKeyColumns() {
		return keyColumns;
	}
	
	public ColumnInfo[] getColumns() {
		return tableColumns;
	}
	
	public boolean columnExists(String columnName) {
		if (columnName==null) {
			return false;
		}
		for (ColumnInfo col: tableColumns) {
			if (columnName.equalsIgnoreCase(col.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public FieldInfo getField(String fieldName) {
		if (fieldName==null) {
			return null;
		}
		fieldName = fieldName.toLowerCase();
		FieldInfo field = fieldMap.get(fieldName);
		if (field==null) {
			field = this.columnFieldMap.get(fieldName);
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
	
	public String[] getNotKeyFields() {
		List<String> rtFields = new ArrayList<>();
		for (FieldInfo field: fields) {
			if (!field.isForeignKey()) {
				rtFields.add(field.getName());
			}
		}
		return rtFields.toArray(new String[]{});
	}
	
	public List<String> getNoFieldColumns() {
		FieldInfo field;
		List<String> cols = new ArrayList<>();
		for (ColumnInfo col: this.tableColumns) {
			field = this.getField(col.getName()) ;
			if (field==null) {
				cols.add(col.getName());
			}
		}
		return cols;
	}	
	
	public void print() {
		System.out.println("\n载入模型："+objectClass.getSimpleName());
		System.out.println("对应数据库表："+this.fullTableName);
		System.out.printf("\n%10s%12s%16s\n", "属性名", "属性类别", "字段名[类型]");
		System.out.println("---------------------------------------------------------");
		String colName, fieldType;
		ColumnInfo col;
		for (FieldInfo field: fields) {
			col = field.getColumn();
			colName = "-";
			if (col!=null) {
				colName = col.getName()+" ["+col.getType()+":"+col.getTypeName()+"]";
			}
			fieldType = field.isNormal()?"普通":"映射";
			System.out.printf("%16s %8s %22s\n", 
					field.getName(), fieldType, colName);
		}
		List<String> noFieldColumns = this.getNoFieldColumns();
		String noFieldCols = StringUtils.link(noFieldColumns, ",");
		System.out.println("no field columns: "+noFieldCols);
	}
	
	public Object getFieldValue(Object obj, String fieldName) {
		if (obj==null) {
			return null;
		}
		FieldInfo fi = this.getField(fieldName);
		if (fi==null) {
			return null;
		}
		return fi.getValue(obj);
	}
	
	public String getStringValue(Object obj, String fieldName) {
		Object value = getFieldValue(obj, fieldName);
		return Convertor.toString(value);
	}
	
}
