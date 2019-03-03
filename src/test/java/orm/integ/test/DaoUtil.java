package orm.integ.test;

import orm.integ.dao.DBUtil;
import orm.integ.dao.DataAccessObject;
import orm.integ.dao.JdbcConfig;

public class DaoUtil {

	private static DataAccessObject dao;
	
	public static DataAccessObject getDao() {
		if (dao==null) {
			JdbcConfig config = createMySQLConfig();
			dao = DBUtil.createDAO(config);
		}
		return dao;
	}
	
	static JdbcConfig createMySQLConfig() {
		JdbcConfig config = new JdbcConfig();
		config.setDriverClass("com.mysql.jdbc.Driver");
		config.setUrl("jdbc:mysql://localhost:3306/study?useUnicode=true&autoReconnect=true&failOverReadOnly=false&characterEncoding=utf-8");
		config.setUserName("root");
		config.setPassword("123456");
		return config;
	}
	
	static JdbcConfig createOracleConfig() {
		JdbcConfig config = new JdbcConfig();
		config.setDriverClass("oracle.jdbc.driver.OracleDriver");
		config.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
		config.setUserName("study2");
		config.setPassword("123456Ab");
		return config;
	}
	
	static JdbcConfig createSQLServerConfig() {
		JdbcConfig config = new JdbcConfig();
		config.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		config.setUrl("jdbc:sqlserver://localhost:1433; DatabaseName=study");
		config.setUserName("sa");
		config.setPassword("123456");
		return config;
	}
	
	static JdbcConfig createPostgreSQLConfig() {
		JdbcConfig config = new JdbcConfig();
		config.setDriverClass("org.postgresql.Driver");
		config.setUrl("jdbc:postgresql://localhost:5432/postgres");
		config.setUserName("postgres");
		config.setPassword("123456");
		return config;
	}	
	
}
