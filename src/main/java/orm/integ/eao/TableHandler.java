package orm.integ.eao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import orm.integ.dao.DBUtil;
import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.Where;
import orm.integ.eao.model.FieldInfo;
import orm.integ.eao.model.FromOrmHelper;
import orm.integ.eao.model.RecordObject;
import orm.integ.eao.model.TableModel;
import orm.integ.eao.transaction.ChangeFactory;
import orm.integ.eao.transaction.FieldChange;
import orm.integ.utils.Convertor;
import orm.integ.utils.IntegError;
import orm.integ.utils.MyLogger;

public abstract class TableHandler<T extends RecordObject> {

	protected DataAccessObject dao;
	protected TableModel model;
	
	protected void init(DataAccessObject dao, TableModel model) {
		this.dao = dao;
		this.model = model;
	}
	
	class TableRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rset, int row) throws SQLException {
			T obj = null;
			try {
				obj = readRowValues(rset);
				FromOrmHelper.setFromOrm(obj);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return obj;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected T readRowValues(ResultSet rset) throws Exception  {
		T object = (T) model.getObjectClass().newInstance();
		FieldInfo field;
		Object value;
		ResultSetMetaData metaData;
		metaData = rset.getMetaData();
		int colCount = metaData.getColumnCount();
		String colName;
		for (int i=1; i<=colCount; i++) {
			colName = metaData.getColumnName(i);
			field = model.getField(colName);
			if (field!=null) {
				if (field.getField().getType()==int.class) {
					value = rset.getInt(i);
				}
				else {
				// 2019-04-18 MySQL当字段类型为 tinyint(1) 时, rset.getObject(i) 返回 Boolean, 奇怪
					value = rset.getObject(i);
				}
				field.setValue(object, value);
			}
		}
		return object;
	}

	protected Map<String, Object> calcUpdateFields(RecordObject old, RecordObject now) {
		List<FieldChange> fieldChanges = ChangeFactory.findDifferents(old, now);
		List<String> fields = new ArrayList<>();
		String fieldName, colName;
		Object value;
		FieldInfo field ;
		Map<String, Object> updateFields = new HashMap<String, Object>();
		List<String> lines = new ArrayList<>();
		String va, vb;
		for(FieldChange fc:fieldChanges) {
			fieldName = fc.getFieldName();
			field = model.getField(fieldName);
			if (field!=null && field.columnExists()) {
				vb = Convertor.toString(fc.getBeforeValue());
				va = Convertor.toString(fc.getAfterValue());
				lines.add(fieldName+":"+vb+"->"+va);
				fields.add(fieldName);
				colName = field.getColumnName();
				value = field.getValue(now);
				updateFields.put(colName, value);
			}
		}
		String className = model.getObjectClass().getSimpleName();
		MyLogger.print(className+" entity changed", lines, ";  ");
		return updateFields;
	}
	
	public void insert(RecordObject obj) {
		FieldInfo[] fields = model.getFields();
		Map<String, Object> colValues = new HashMap<>();
		Object value;
		Class<?> dbDataType;
		if (obj.getCreateTime()==null) {
			obj.setCreateTime(new Date());
		}
		for (FieldInfo field: fields) {
			if (field.columnExists()) {
				value = field.getValue(obj);
				if (value!=null) {
					dbDataType = DBUtil.getDataType(field.getColumn());
					value = Convertor.translate(value, dbDataType);
					colValues.put(field.getColumnName(), value);
				}
			}
		}
		dao.insert(model.getTableName(), colValues);	
		FromOrmHelper.setFromOrm(obj);
	}
	
	@SuppressWarnings("unchecked")
	public T queryOld(T obj) {
		Where keyWhere = getKeyWhere(obj);
		String sql = "select * from "+model.getFullTableName()+" "+keyWhere.toString();
		T oldObj = (T) dao.queryForObject(sql, keyWhere.getValues(), model.getObjectClass());
		return oldObj;
	}
	
	private Where getKeyWhere(T obj) {
		Where where = new Where();
		Object value;
		for (FieldInfo fi: model.getFields()) {
			if (fi.isKey()) {
				value = fi.getValue(obj);
				if (value==null) {
					throw new IntegError(fi.getName()+"为空");
				}
				where.addItem(fi.getColumnName()+"=?", value);
			}
		}
		if (where.isEmpty()) {
			String className = model.getObjectClass().getSimpleName();
			throw new IntegError(className+" 未设置主键");
		}
		return where;
	}
	
	public void update(T obj, String[] fields) {
		Where where = this.getKeyWhere(obj);

		Map<String, Object> values = new HashMap<>();
		FieldInfo fi;
		Object value;
		for (String fieldName: fields) {
			fi = model.getField(fieldName);
			if (fi!=null && !fi.isKey() && fi.columnExists()) {
				value = fi.getValue(obj);
				values.put(fi.getColumnName(), value);
			}
		}
		dao.update(model.getFullTableName(), values, where);
	}
	
	public void update(T obj) {
		Where where = this.getKeyWhere(obj);
		Map<String, Object> values = new HashMap<>();
		Object value;
		for (FieldInfo fi: model.getFields()) {
			if (!fi.isKey() && fi.columnExists() 
					&& !fi.getName().equals("createTime")) {
				value = fi.getValue(obj);
				values.put(fi.getColumnName(), value);
			}
		}
		dao.update(model.getFullTableName(), values, where);
	}
	
}
