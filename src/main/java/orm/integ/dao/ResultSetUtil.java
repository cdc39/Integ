package orm.integ.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import orm.integ.utils.ObjectHandler;
import orm.integ.utils.Record;
import orm.integ.utils.StringUtils;

public class ResultSetUtil {

	public static <T> T toObject(ResultSet rset, Class<T> clazz) {
		T obj = null;
		try {
			obj = clazz.newInstance();
			ResultSetMetaData md = rset.getMetaData();
			int cnt = md.getColumnCount();
			String colName, fieldName;
			ObjectHandler obh = new ObjectHandler(obj);
			Object value;
			for (int i=1; i<=cnt; i++) {
				colName = md.getColumnName(i);
				fieldName = StringUtils.underline2hump(colName);
				value = readValue(rset, i);
				obh.setValue(fieldName, value);
			}
			return obj;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static Record toRecord(ResultSet rset) {
		Record rec = new Record();
		try {
			ResultSetMetaData md = rset.getMetaData();
			int cnt = md.getColumnCount();
			String colName, fieldName;
			Object value;
			for (int i=1; i<=cnt; i++) {
				colName = md.getColumnName(i);
				fieldName = StringUtils.underline2hump(colName);
				value = readValue(rset, i);
				rec.put(fieldName, value);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return rec;
	}
	
	public static Object readValue(ResultSet rset, int col) throws SQLException {
		Object value;
		int type = rset.getMetaData().getColumnType(col);
		if (type==Types.BIT || type==Types.TINYINT || type==Types.BOOLEAN) {
			value = rset.getInt(col);
		}
		else {
			value = rset.getObject(col);
		}
		return value;
	}
	
}
