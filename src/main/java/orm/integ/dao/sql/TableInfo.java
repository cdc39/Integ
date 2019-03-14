package orm.integ.dao.sql;

public interface TableInfo {

	public String getTableName() ;
	
	public String getFullTableName();
	
	public String[] getKeyColumns();
	
}
