package orm.integ.dao;

public class JdbcConfig {
	
	public static final String DRIVER = "jdbc.driverClassName";
	public static final String URL = "jdbc.url";
	public static final String USERNAME = "jdbc.username";
	public static final String PASSWORD = "jdbc.password";

	private String driverClass;
	private String userName;
	private String password;
	private String url;
	
	
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClassName) {
		this.driverClass = driverClassName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getSchemaByUrl(String url) {
		int pos = url.indexOf('?');
		String temp = url.substring(0, pos);
		System.out.println(" ::temp="+temp);
		pos = temp.lastIndexOf('/');
		String schema = temp.substring(pos+1);
		return schema;
	}
	
}
