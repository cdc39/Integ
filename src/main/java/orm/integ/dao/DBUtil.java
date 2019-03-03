package orm.integ.dao;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import orm.integ.dao.sql.SqlBuilder;
import orm.integ.utils.IntegError;
import orm.integ.utils.StringUtils;

public class DBUtil {

	public static SqlBuilder getDialectByDriverClass(String driverClass) {
		String driver = driverClass.toLowerCase();
		for (DBType dbt: DBType.values()) {
			if (driver.indexOf(dbt.key())>=0) {
				try {
					return dbt.getDialetClass().newInstance();
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}
		throw new IntegError("不能匹配的数据库驱动 "+driverClass);
	}
	
	
	public static DataAccessObject createDAO(JdbcConfig config)  {
		BasicDataSource bds = new BasicDataSource() ;
		bds.setDriverClassName(config.getDriverClass());
		bds.setUsername(config.getUserName());
		bds.setPassword(config.getPassword());
		bds.setUrl(config.getUrl());
		
		SqlBuilder dialect = getDialectByDriverClass(config.getDriverClass());
		
		DataAccessObject dao = new DataAccessObject(bds, dialect);
		dao.testConnect();
		return dao;
	}
	
	public static DataAccessObject createDAO(File configFile) throws Exception {
		JdbcConfig config = readJdbcConfig(configFile);
		return createDAO(config);
	}
	
	private static JdbcConfig readJdbcConfig(File file)  {
		Map<String, String> params = readProperties(file);
		JdbcConfig config = new JdbcConfig();
		config.setDriverClass(params.get(JdbcConfig.DRIVER));
		config.setUserName(params.get(JdbcConfig.USERNAME));
		config.setPassword(params.get(JdbcConfig.PASSWORD));
		String url = params.get(JdbcConfig.URL);
		url = StringUtils.removeBlank(url);
		config.setUrl(url);
		return config;
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getDataType(ColumnInfo col) {
		int type = col.getType();
		if (type==Types.INTEGER || type==Types.TINYINT || type==Types.SMALLINT || type==Types.BIT ) {
			return Integer.class;
		}
		else if (type==Types.NUMERIC) {
			if (col.getScale()==0) {
				if (col.getPrecision()<10) {
					return Integer.class;
				}
				else {
					return Long.class;
				}
			}
			else {
				return Double.class;
			}
		}
		else if (type==Types.BIGINT){
			return Long.class;
		}
		else if (type==Types.VARCHAR) {
			return String.class;
		}
		else if (type==Types.DATE || type==Types.TIMESTAMP ) {
			return Date.class;
		}
		else {
			System.out.println("DBUtil.getDataType: type="+type+" 待细分处理");
			return String.class;
		}
	}
	
	
	private static Map<String, String> readProperties(File file) {
		Map<String, String> params = new HashMap<>();
		Properties pro = new Properties();
		try {
			FileInputStream in = new FileInputStream(file);
			pro.load(in);
			in.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Object value;
		for(Object key: pro.keySet()) {
			value = pro.get(key);
			//System.out.println("key="+key+", value="+value);
			params.put(key.toString(), value.toString());
		}
		return params;
	}
	
	
}
