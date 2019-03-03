package orm.integ.dao;

import orm.integ.dao.dialect.MySQL;
import orm.integ.dao.dialect.Oracle;
import orm.integ.dao.dialect.PostgreSQL;
import orm.integ.dao.dialect.SQLServer;
import orm.integ.dao.sql.SqlBuilder;

public enum DBType {

	MySQL("mysql", MySQL.class),
	Oracle("oracle", Oracle.class),
	SQLServer("sqlserver", SQLServer.class),
	PostgreSQL("postgresql", PostgreSQL.class);
	
	private DBType(String key, Class<? extends SqlBuilder> dialetClass) {
		this.key = key;
		this.dialetClass = dialetClass;
	}
	
	private String key;
	private Class<? extends SqlBuilder> dialetClass;
	
	public String key() {
		return key;
	}
	
	public Class<? extends SqlBuilder> getDialetClass() {
		return dialetClass;
	}
	
}
