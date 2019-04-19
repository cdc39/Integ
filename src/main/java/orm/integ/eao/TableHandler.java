package orm.integ.eao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import orm.integ.dao.DBUtil;
import orm.integ.dao.DataAccessObject;
import orm.integ.eao.model.FieldInfo;
import orm.integ.eao.model.FromOrmHelper;
import orm.integ.eao.model.RecordObject;
import orm.integ.eao.model.TableModel;
import orm.integ.eao.transaction.ChangeFactory;
import orm.integ.eao.transaction.FieldChange;
import orm.integ.utils.Convertor;
import orm.integ.utils.MyLogger;

public class TableHandler {

	DataAccessObject dao;
	
	public TableHandler(DataAccessObject dao) {
		this.dao = dao;
	}
	
	class TableRowMapper implements RowMapper {

		TableModel model;
		
		TableRowMapper(TableModel model) {
			this.model = model;
		}
		
		@Override
		public Object mapRow(ResultSet rset, int row) throws SQLException {
			RecordObject obj = null;
			try {
				obj = readRowValues(model, rset);
				FromOrmHelper.setFromOrm(obj);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return obj;
		}
		
	}
	
	protected RecordObject readRowValues(TableModel model, ResultSet rset) throws Exception  {
		RecordObject object = (RecordObject) model.getObjectClass().newInstance();
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

	protected Map<String, Object> calcUpdataFields(RecordObject old, RecordObject now, TableModel model) {
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
	
	protected void insert(RecordObject obj, TableModel table) {
		FieldInfo[] fields = table.getFields();
		Map<String, Object> colValues = new HashMap<>();
		Object value;
		Class<?> dbDataType;
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
		dao.insert(table.getTableName(), colValues);	
		FromOrmHelper.setFromOrm(obj);
	}
	
}
