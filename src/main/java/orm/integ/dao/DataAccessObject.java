package orm.integ.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.SqlBuilder;
import orm.integ.dao.sql.StatementAndValue;
import orm.integ.dao.sql.TabQuery;
import orm.integ.utils.Convertor;
import orm.integ.utils.IntegError;
import orm.integ.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class DataAccessObject {

	static Logger log = Logger.getLogger(DataAccessObject.class);
	static {
		log.setLevel(Level.ALL);
	}
	
	private final DataSource dataSource;
	private final SqlBuilder dialect;
	private final JdbcTemplate jdbcTemplate;
	
	DataAccessObject(DataSource dataSource, SqlBuilder dialect) {
		this.dataSource = dataSource;
		this.dialect = dialect;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
//	public SqlDialect getSqlBuilder(QueryRequest qreq) {
//		return new Dialect4MySQL(qreq);
//	}
	
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ResultSetMetaData getTableMetaData(String tableName) throws SQLException  {
		String sql = "select * from "+tableName+" where 1=0";
		Connection conn = null;
		ResultSet rset = null;
		Statement stmt = null;
		try
		{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rset.getMetaData();
			return metaData;
		}
		finally{
			if (rset!=null) {
				rset.close();
			}
			if (stmt!=null) {
				stmt.close();
			}
		}
	}
	
	public void testConnect() {
		String sql = dialect.getTestSql();
		getJdbcTemplate().queryForRowSet(sql);
	}
	
	public boolean tableExistTest(String tableName) {
		String sql = "select count(*) from "+tableName+" where 1=0";
		try
		{
			jdbcTemplate.queryForInt(sql);
			return true;
		}
		catch(Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	public void testQuery(String sql, Object...values) {
		this.jdbcTemplate.queryForRowSet(sql, values);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	public int queryForInt(String sql, Object... values) {
		printSql(sql, values);
		int n = getJdbcTemplate().queryForInt(sql, values);
		if (n>0) {
			log.debug("result="+n);
		}
		return n;
	}
	public long queryForLong(String sql, Object... values) {
		printSql(sql, values);
		long n = getJdbcTemplate().queryForLong(sql, values);
		if (n>0) {
			log.debug("result="+n);
		}
		return n;
	}
	public String queryForString(String sql, Object... values) {
		Object value = getJdbcTemplate().queryForObject(sql, values, String.class);
		return (String)value;
	}
	public Date queryForDate(String sql, Object...values) {
		Object value = getJdbcTemplate().queryForObject(sql, values, Timestamp.class);
		return (Date)value;
	}

	public int queryCount(String tableName, String whereStmt, Object... values) {
		whereStmt = SqlBuilder.formatWhereStmt(whereStmt);
		String sql = "select count(*) from "+tableName+whereStmt;
		return queryForInt(sql, values);
	}
	
	public int queryCount(QueryRequest request) {
		String sql = dialect.makeQueryCountSql(request);
		Object[] values = request.getValues();
		return queryForInt(sql, values);
	}
	
	public List<String> queryIds(QueryRequest request) {
		String sql = dialect.makeQueryIdsSql(request);
		return queryIds(sql, request.getValues());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> queryIds(String sql, Object...values) {
		printSql(sql, values);
		List<String> list = getJdbcTemplate().query(sql, values, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rset, int row) throws SQLException { 
				return rset.getString(1);
			}
		});
		log.debug("Get "+list.size()+" items");
		return list;
	}
	
//	@SuppressWarnings("unchecked")
//	public String queryFirstId(TabQuery model) {
//		String sql = getSqlBuilder(model).makeQueryFirstIdSql();
//		Object[] values = model.linkValues();
//		log.debug("queryFirstId: " + toString(sql, values));
//		List<String> list = getJdbcTemplate().query(sql, values, new RowMapper(){
//			@Override
//			public Object mapRow(ResultSet rset, int row) throws SQLException { 
//				return rset.getString(1);
//			}
//		});
//		log.debug("Get "+list.size()+" items");
//		String firstId = list.size()>0?list.get(0):null;
//		return firstId;
//	}
	
	public int executeSql(String sql, Object... values) {
		printSql(sql, values);
		date2Timestamp(values);
		int rt = getJdbcTemplate().update(sql, values);
		log.debug(rt>0?("affect "+rt+" record"):null);
		return rt;
	}
	

	
	private void date2Timestamp(Object[] values) {
		for (int i=0; i<values.length; i++) {
			if (values[i] instanceof Date) {
				values[i] = Convertor.toTimestamp((Date)values[i]);
			}
		}
	}
	
	public List query(String sql, Object[] values, RowMapper rowMapper) {
		if (values==null) {
			values = new Object[]{};
		}
		printSql(sql, values);
		List list = getJdbcTemplate().query(sql, values, rowMapper);
		if (list==null) {
			list = new ArrayList();
		}
		//System.out.println(sql);
		log.debug("get "+list.size()+" items");
		return list;
	}
	
	public List query(StatementAndValue sql, RowMapper rowMapper) {
		return query(sql.getStatement(), sql.getValues(), rowMapper);
	}	
	
	@SuppressWarnings("unchecked")
	public <X> X querySingle(String sql, Object[] values, RowMapper rowMapper) {
		List list = query(sql, values, rowMapper);
		if (list.size()==1) {
			return (X) list.get(0);
		}
		else {
			return null;
		}
	}

	public int getNextIntId(String tableName, String keyColumn) {
		String sql = "select ifnull(max("+keyColumn+"),0)+1 from "+tableName;
		int nextId1 = queryForInt(sql);
		return nextId1;
	}
	
	private void printSql(String sql, Object... values) {
		if (values!=null&& values.length>0) {
			sql = sql + "|" + StringUtils.link(values, ",");
		}
		log.info(sql);
	}

	public Map statQuery(String sql, Object... values) {
		Map map = jdbcTemplate.queryForMap(sql, values);
		return map;
	}
	
	public double queryForDouble(String sql, Object... values) {
		Map result = statQuery(sql, values); 
		Object[] rtValues = result.values().toArray();
		if (rtValues==null || rtValues.length==0) {
			return 0;
		}
		else {
			return Convertor.toDouble(rtValues[0], 0);
		}
	}
	
	/*
	protected void executeSqls(final List<String> sqls) {
		transactionTemplate.execute (
				new TransactionCallback()
				{
					public Object doInTransaction(TransactionStatus status)
					{
						JdbcTemplate jt = new JdbcTemplate(dataSource);
						for (String sql: sqls) {
							System.out.println("execute sql: "+ sql);
							jt.execute(sql);
							//throw new RuntimeException("这是异常测试");
						}
						return null;
					}
				}
		);
	}
	*/
	

	public void addBatch(PreparedStatement ps, Object[] values) throws Exception {
		Object value;
		//Printer.get().print(values, "values");
		for (int i=1; i<=values.length; i++) {
			value = values[i-1];
			if (value==null) {
				ps.setString(i, null);
			}
			else if (value instanceof String) {
				ps.setString(i, (String)value);
			}
			else if (value instanceof Integer) {
				ps.setInt(i, (Integer)value);
			}
			else if (value instanceof Long) {
				ps.setLong(i, (Long)value);
			}
			else if (value instanceof Float) {
				ps.setFloat(i, (Float)value);
			}
			else if (value instanceof Double) {
				ps.setDouble(i, (Double)value);
			}
			else if (value instanceof Date) {
				Date date = (Date)value;
				Timestamp time = new Timestamp(date.getTime());
				ps.setTimestamp(i, time);
			}
			else if (value instanceof Timestamp) {
				ps.setTimestamp(i, (Timestamp)value);
			}
		}
		ps.addBatch();
	}
	

	
	public Object queryForObject(String sql, Object[] args, RowMapper rowMapper) {
		return jdbcTemplate.queryForObject(sql, args, rowMapper);
	}

	public List query(QueryRequest request, RowMapper rowMapper) {
		String sql = dialect.makePageQuerySql(request);
		return this.query(sql, request.getValues(), rowMapper);
	}

	public void insert(String tableName, Map<String, ?> cols) {
		String[] colNames = cols.keySet().toArray(new String[0]);
		Object value;
		List<Object> values = new ArrayList<>();
		for (String colName:colNames) {
			value = cols.get(colName);
			values.add(value);
		}
		String colNamesStr = StringUtils.link(colNames, ", ");
		String paramsStr = StringUtils.repeat("?", ",", colNames.length);
		String sql = "insert into "+tableName+" ("+colNamesStr+") values ("+paramsStr+")";
		this.executeSql(sql, values.toArray());
	}
	
	public void insert(String tableName, String[] cols, Object[] values) {
		if (cols.length!=values.length) {
			throw new IntegError("字段数和数值数量不一致");
		}
		Map<String, Object> colValues = new HashMap<>();
		for (int i=0; i<cols.length; i++) {
			colValues.put(cols[i], values[i]);
		}
		insert(tableName, colValues);
	}
	
	public void deleteById(String tableName, String keyColumn, Object id) {
		String sql = "delete from "+tableName+" where "+keyColumn+"=?";
		this.executeSql(sql, id);
	}

	public int update(String tableName, Map<String,Object> updateValues, 
			StatementAndValue whereItem) {
		if (whereItem==null || isNull(whereItem.getStatement())) {
			throw new IntegError("where item can't be null in update!");
		}
		String[] updateCols = updateValues.keySet().toArray(new String[0]);
		
		List<String> columns = new ArrayList<>();
		for (String col: updateCols) {
			columns.add(col+"=?");
		}
		String colsStr = StringUtils.link(columns, ", ");
		String sql = "update "+tableName+" set "+colsStr+" where "+whereItem.getStatement();
		
		Object[] whereValues = whereItem.getValues();
		Object[] values2 = new Object[updateCols.length+whereValues.length];
		String colName;
		for (int i=0; i<updateCols.length; i++) {
			colName = updateCols[i];
			values2[i] = updateValues.get(colName);
		}
		for (int i=0; i<whereValues.length; i++) {
			values2[updateCols.length+i] = whereValues[i];
		}
		
		return this.executeSql(sql, values2);
	}
	
	public int getNextOrderNo(String tableName, String groupColumn, String groupId) {
		String sql = "select ifnull(max(order_no),0) from "+tableName+" where "+groupColumn+"=?";
		int maxNo = this.queryForInt(sql, groupId);
		return maxNo+1;
	}
	public int getNextOrderNo(String tableName) {
		String sql = "select ifnull(max(order_no),0) from "+tableName;
		int maxNo = this.queryForInt(sql);
		return maxNo+1;
	}
	public int getNextSeqNo(String tableName, String seqCol) {
		String sql = "select ifnull(max("+seqCol+"),0) from "+tableName;
		int maxNo = this.queryForInt(sql);
		return maxNo+1;
	}
	
	public List<ColumnInfo> getTableColumns(String tableName) { 
		String sql ;
		Connection conn = null;
		Statement stmt;
		ResultSet rset;
		List<ColumnInfo> colList = new ArrayList<>();
		try {
			conn = getDataSource().getConnection();
			stmt = conn.createStatement();
			sql = "select * from "+tableName+" where 1=0";
			rset = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rset.getMetaData();
			int colNum = metaData.getColumnCount();
			ColumnInfo col;
			for (int i=1; i<=colNum; i++) {
				col = new ColumnInfo();
				col.setName(metaData.getColumnName(i));
				col.setType(metaData.getColumnType(i));
				col.setTypeName(metaData.getColumnTypeName(i));
				col.setClassName(metaData.getColumnClassName(i));
				col.setScale(metaData.getScale(i));
				col.setPrecision(metaData.getPrecision(i));
				col.setDisplaySize(metaData.getColumnDisplaySize(i));
				colList.add(col);
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return colList;
		
	}

	public Object queryById(String tableName, String keyColumn, Object id, RowMapper rowMapper) {
		String sql = "select * from "+tableName+" where "+keyColumn+"=?";
		List list = query(sql, new Object[]{id}, rowMapper);
		if (list==null||list.size()==0) {
			return null;
		}
		else {
			return list.get(0);
		}
	}
	
	public List queryByIds(String tableName, String keyColumn, Object[] ids, RowMapper rowMapper) {

		if (ids==null || ids.length==0) {
			return new ArrayList<>();
		}
		
		String sql = "select * from "+tableName+" where "+keyColumn+" in (:ids)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate =   
			    new NamedParameterJdbcTemplate(jdbcTemplate);  
		MapSqlParameterSource parameters = new MapSqlParameterSource(); 
		parameters.addValue("ids",Arrays.asList(ids));
		List list = namedParameterJdbcTemplate.query(sql, parameters, rowMapper);
		return list;
		
	}

	public int update(TabQuery req, String setStmt) {
		String whereStmt = req.getWhere().toString();
		String sql = "update "+req.getTableName()+" set "+ setStmt+whereStmt;
		return executeSql(sql, req.getValues());
	}

	public boolean isNull(String s) {
		return s==null||s.trim().equals("");
	}


	
}
