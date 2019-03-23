package orm.integ.eao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.jdbc.core.RowMapper;

import orm.integ.dao.DBUtil;
import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.QueryRequest;
import orm.integ.dao.sql.StatementAndValue;
import orm.integ.dao.sql.TabQuery;
import orm.integ.eao.cache.MemoryCache;
import orm.integ.eao.cache.NoExistsCache;
import orm.integ.eao.cache.QueryManager;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.model.EntityModelBuilder;
import orm.integ.eao.model.FieldInfo;
import orm.integ.eao.model.FieldMapping;
import orm.integ.eao.model.ForeignUse;
import orm.integ.eao.model.FromOrmHelper;
import orm.integ.eao.model.PageData;
import orm.integ.eao.model.Record;
import orm.integ.eao.model.Relation;
import orm.integ.eao.model.RelationModel;
import orm.integ.eao.model.TableModel;
import orm.integ.eao.model.TableModels;
import orm.integ.eao.transaction.ChangeFactory;
import orm.integ.eao.transaction.DataChange;
import orm.integ.eao.transaction.DataChangeListener;
import orm.integ.eao.transaction.FieldChange;
import orm.integ.eao.transaction.TransactionManager;
import orm.integ.utils.Convertor;
import orm.integ.utils.IntegError;
import orm.integ.utils.MyLogger;
import orm.integ.utils.StringUtils;

public class EntityAccessObject<T extends Entity> {

	public static int queryByIdsCount = 0;
	
	private DataAccessObject dao;
	private EntityModel em;
	private final Class<T> entityClass;
	private final RowMapper rowMapper;
	private final EaoAdapter<T> adapter;
	private final MemoryCache<T> cache = new MemoryCache<>();
	private final NoExistsCache notExistsCache = new NoExistsCache();
	private final QueryManager<T> queryManager ;
	private final List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public EntityAccessObject(EaoAdapter<T> adapter) {
		
		this.adapter = adapter;
		this.dao = adapter.getDao();
        Type t = adapter.getClass().getGenericSuperclass();
        Type[] ts = ((ParameterizedType) t).getActualTypeArguments();
		entityClass = (Class<T>)ts[0];
		
		
		EntityModelBuilder emBuilder = new EntityModelBuilder(entityClass, dao);
		adapter.setEntityConfig(emBuilder);
		em = emBuilder.buildModel();
		
		queryManager = new QueryManager<T>(dao);
		
		dataChangeListeners.add(cache);
		dataChangeListeners.add(queryManager);
		dataChangeListeners.add(notExistsCache);
		
		Eaos.addEao(this);
		
		rowMapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rset, int row) throws SQLException {
				T entity = null;
				try {
					entity = (T) readRowValues(em, rset);
					FromOrmHelper.setFromOrm(entity);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return entity;
			}
		};
		
	}
	
	public DataAccessObject getDAO() {
		return dao;
	}
	
	public EntityModel getEntityModel() {
		return em;
	}
	
	public QueryManager<T> getQueryManager() {
		return this.queryManager;
	}
	
	public DataChangeListener[] getDataChangeListeners() {
		return dataChangeListeners.toArray(new DataChangeListener[0]);
	}
	
	private Object readRowValues(TableModel model, ResultSet rset) throws Exception  {
		Object object = model.getObjectClass().newInstance();
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
				value = rset.getObject(i);
				field.setValue(object, value);
			}
		}
		return object;
	}

	public T getById(Object id) {
		if (id==null) {
			return null;
		}
		T en = cache.get(id.toString());
		if (en==null) {
			if (!notExistsCache.isNotExistId(id)) {
				en = load(id);
				if (en!=null) {
					putToCache(en);
				}
				else {
					notExistsCache.signNotExist(id);
				}
			}
		}
		return en;
	}
	
	@SuppressWarnings("unchecked")
	public T load(Object id) {
		T en = (T) dao.queryById(em.getFullTableName(), em.getKeyColumn(), id, rowMapper);
		return en;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> getByIds(Collection ids) {
		LinkedHashSet set = new LinkedHashSet();
		T en;
		for (Object id: ids) {
			en = cache.get(id.toString());
			if (en==null) {
				set.add(id);
			}
		}
		if (set.size()>0) {
			List<T> listNew = dao.queryByIds(em.getFullTableName(), em.getKeyColumn(), set.toArray(), rowMapper);
			queryByIdsCount++;
			for (T obj: listNew) {
				putToCache(obj);
			}
		}
		List<T> list = new ArrayList<>();
		for (Object id: ids) {
			list.add(cache.get(id));
		}
		return list;
	}
	
	protected void putToCache(T entity) {
		if (entity!=null) {
			cache.put(entity);
			adapter.fillExtendFields(entity);
		}
	}

	public List<T> getAll(int maxReturn) {
		TabQuery query = new TabQuery();
		query.setMaxReturnRowNum(maxReturn);
		query.setPageInfo(1, maxReturn);
		return this.query(query);
	}
	
	public List<T> getAll() {
		return getAll(10000);
	}
	
	protected T getFirst(List<T> list) {
		return list==null||list.size()==0?null:list.get(0);
	}
	
	public T queryFirst(QueryRequest req) {
		req.setPageInfo(1, 1);
		List<T> list = this.query(req);
		return this.getFirst(list);
	}
	
	public T queryFirst(String where, String order, Object... values) {
		TabQuery req = new TabQuery();
		req.setPageInfo(1, 1);
		req.addWhereItem(where, values);
		req.setOrder(order);
		return queryFirst(req);
	}
	
	public T querySingle(String where, Object... values) {
		return queryFirst(where, null, values);
	}
	
	protected void insert(Object obj, TableModel table) {
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
	}
	
	public void insert(T entity) {
		String id = entity.getId();
		if (id==null) {
			id = this.createNewIdNoRepeat();
			entity.setId(id);
		}
		entity.setCreateTime(new Date());
		DataChange change = ChangeFactory.newInsert(entity);
		adapter.beforeChange(change);
		insert(entity, em);
		FromOrmHelper.setFromOrm(entity);
		afterChange(change);
		putToCache(entity);
		adapter.afterChange(change);
	}
	
	protected String createNewIdNoRepeat() {
		String newId;
		int testCount = 0, count;
		do {
			newId = adapter.createNewId().toString();
			count = queryCount(em.getKeyColumn()+"=?", newId);
			testCount++;
		} while (count>0 && testCount<10);
		if (testCount>=10) {
			throw new IntegError("产生主键值程序有错误，已连续产生了"+testCount+"个重复主键！");
		}
		return newId;
	}
	
	public int queryCount(String whereStmt, Object... values) {
		TabQuery tq = new TabQuery(em);
		tq.addWhereItem(whereStmt, values);
		return queryManager.queryCount(tq);
	}
	
	public void deleteById(Object id, boolean checkForeignUse) {
		T entity = this.getById(id);
		if (checkForeignUse) {
			testForienUseBeforeDelete(id);
		}
		DataChange change = ChangeFactory.newDelete(entity);
		adapter.beforeChange(change);
		dao.deleteById(em.getFullTableName(), em.getKeyColumn(), id);
		afterChange(change);
		adapter.afterChange(change);
	}
	
	public void deleteById(Object id) {
		deleteById(id, true);
	}
	
	@SuppressWarnings("rawtypes")
	public void testForienUseBeforeDelete(Object id) {
		List<FieldInfo> fkFields = TableModels.getForeignKeyFields(em.getEntityClass());
		for (FieldInfo fkField: fkFields) {
			if (fkField.columnExists()) {
				EntityAccessObject eao = Eaos.getEao(fkField.getOwnerClass());
				int count = eao.queryCount(fkField.getColumnName()+"=?", id);
				if (count>0) {
					String tabName = eao.getEntityModel().getTableName();
					throw new IntegError(tabName+"."+fkField.getColumnName()+"="+id+" 已有数据,不能删除");
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<ForeignUse> scanForeignUse(Object id) {
		List<ForeignUse> uses = new ArrayList<>();
		List<FieldInfo> fkFields = TableModels.getForeignKeyFields(em.getEntityClass());
		for (FieldInfo fkField: fkFields) {
			if (fkField.columnExists()) {
				EntityAccessObject eao = Eaos.getEao(fkField.getOwnerClass());
				int count = eao.queryCount(fkField.getColumnName()+"=?", id);
				if (count>0) {
					uses.add(new ForeignUse(fkField, count));
				}
			}
		}
		return uses;
	}
	
	@SuppressWarnings("rawtypes")
	public void printForeignUse(Object id) {
		
		List<FieldInfo> fkFields = TableModels.getForeignKeyFields(em.getEntityClass());
		List<ForeignUse> uses = scanForeignUse(id);
		int total = 0;
		for (ForeignUse fu: uses) {
			total+=fu.getRecordCount();
		}
		String info = "实体类 "+em.getEntityClass().getSimpleName()+" 的主键 "
				+ em.getTableName() + "."+em.getKeyColumn()+" 总共对应  "+fkFields.size()+" 外键字段，"
				+ "id值 "+id+" 在 "+uses.size()+" 个外键字段出现了 "+total+" 次：";
		System.out.println("\n"+info);
		for (ForeignUse fu: uses) {
			FieldInfo fkField = fu.getForeignKeyField();
			EntityAccessObject eao = Eaos.getEao(fkField.getOwnerClass());
			String fieldName = eao.getEntityModel().getTableName()+"."+fkField.getColumnName();
			info = fieldName + "=" + id + " -- " + fu.getRecordCount()+" record";
			System.out.println(info);
		}
		System.out.println();
		
	}
	
	public void update(T entity) {
		if (entity==null) {
			MyLogger.printError(new Throwable(), "entity is null!");
			return;
		}
		if (!FromOrmHelper.isFromOrm(entity)) {
			throw new IntegError("entity is not created by integ, can not be update.");
		}
		T old = this.load(entity.getId());
		if (old==null) {
			return;
		}
		DataChange change = ChangeFactory.newUpdate(old, entity);
		adapter.beforeChange(change);

		List<FieldChange> fieldChanges = ChangeFactory.findDifferents(old, entity);
		List<String> fields = new ArrayList<>();
		String fieldName, colName;
		Object value;
		FieldInfo field ;
		Map<String, Object> updateFields = new HashMap<String, Object>();

		for(FieldChange fc:fieldChanges) {
			fieldName = fc.getFieldName();
			field = em.getField(fieldName);
			if (field!=null && field.columnExists()) {
				fields.add(fieldName);
				colName = field.getColumnName();
				value = field.getValue(entity);
				updateFields.put(colName, value);
			}
		}
		if (updateFields.size()==0) {
			return ;
		}
		
		StatementAndValue where = new StatementAndValue(em.getKeyColumn()+"=?", entity.getId());
		
		dao.update(em.getTableName(), updateFields, where);
		afterChange(change);
		adapter.fillExtendFields(entity);
		adapter.afterChange(change);
		
	}

	@SuppressWarnings("unchecked")
	public List<T> query(QueryRequest req) {
		req.setTableInfo(em);
		if (req.getLast()<=QueryRequest.PAGE_QUERY_MAX_RETURN) {
			List<String> ids = queryManager.queryIdList(req);
			return this.getByIds(ids);
		}
		else {
			return dao.query(req, rowMapper);
		}
	}
	
	public List<T> query(String whereStmt, String orderStmt, Object... values) {
		TabQuery query = new TabQuery();
		query.addWhereItem(whereStmt, values);
		query.setOrder(orderStmt);
		return query(query);
	}
	
	public List<T> queryByIds(List<String> ids) {
		List<T> list = new ArrayList<>();
		T entity;
		for (String id: ids) {
			entity = this.getById(id);
			list.add(entity);
		}
		return list;
	}
	
	public PageData pageQuery(QueryRequest req) {
		req.setTableInfo(em);
		List<String> ids = queryManager.queryIdList(req);
		
		int count = queryManager.queryCount(req);

		List<T> list = this.getByIds(ids);
		
		String[] fields = req.getViewFields();
		if (fields==null || fields.length==0) {
			fields = em.getListFields();
		}
		List<Record> viewList = this.toRecords(list, fields);
		return new PageData(viewList, count);
	}
	
	public Record toRecord(T entity, String[] viewFields) {
		Object value;
		Record record = new Record();
		FieldInfo field;
		for (String fieldName:viewFields) {
			field = em.getField(fieldName);
			value = getFieldValue(entity, field, true);
			if (value!=null) {
				record.put(fieldName, value);
			}
		}
		return record;
	}
	
	public Record toDetailRecord(T entity) {
		return toRecord(entity, em.getDetailFields());
	}
	
	Map<Class<? extends Entity>, Set<Object>> getForeignIds(List<T> list) {
		Map<Class<? extends Entity>, Set<Object>> foreignIds = new HashMap<>();
		Set<Object> ids, idsAll = new HashSet<>();
		for (FieldInfo fi: em.getFields()) {
			ids = getValueSet(list, fi.getName());
			idsAll = foreignIds.get(fi.getMasterClass());
			if (idsAll==null) {
				idsAll = new HashSet<>();
				foreignIds.put(fi.getMasterClass(), idsAll);
			}
			idsAll.addAll(ids);
		}
		return foreignIds;
	}
	
	@SuppressWarnings("rawtypes")
	void batchLoadRelEntities(List<T> list, String[] fields) {
		EntityAccessObject relEao ;
		Set<Object> ids;
		Map<Class<? extends Entity>, Set<Object>> foreignIds = getForeignIds(list);
		for (Class clazz: foreignIds.keySet()) {
			relEao = Eaos.getEao(clazz);
			if (relEao!=null) {
				ids = foreignIds.get(clazz);
				relEao.getByIds(ids);
			}
		}
	}
	
	public List<Record> toRecords(List<T> list) {
		return toRecords(list, em.getListFields());
	}
	
	public List<Record> toRecords(List<T> list, String[] fields) {
		
		List<Record> records = new ArrayList<>();
		if (list==null || fields==null || fields.length==0) {
			return records;
		}
		
		batchLoadRelEntities(list, fields);
		
		Record record = new Record();
		for (T entity: list) {
			record = toRecord(entity, fields);
			records.add(record);
		}
		return records;
	}
	
	Set<Object> getValueSet(List<T> list, String fieldName) {
		Set<Object> values = new LinkedHashSet<>();
		Object value;
		for (T entity: list) {
			value = this.getFieldValue(entity, fieldName, false);
			if (value!=null) {
				values.add(value);
			}
		}
		return values;
	}
	
	
	@SuppressWarnings("unchecked")
	public <X> X getFieldValue(T entity, String fieldName)  {
		FieldInfo field = em.getField(fieldName);
		return (X) getFieldValue(entity, field, false);
	}  
	
	@SuppressWarnings("unchecked")
	public <X> X getFieldValue(T entity, String fieldName, boolean dynamicRefresh)  {
		FieldInfo field = em.getField(fieldName);
		return (X) getFieldValue(entity, field, dynamicRefresh);
	}  

	private Object getFieldValue(T entity, FieldInfo field, boolean dynamicRefresh)  {
		if (entity==null||field==null) {
			return null;
		}
		Object value = null;
		if (field.isNormal()) {
			value = field.getValue(entity);
		}
		if (dynamicRefresh) {
			Object value2 = null;
			if (field.isMapping()) {
				value2 = getMappingFieldValue(entity, field);
			}
			if (value2!=null) {
				if (field.isNormal()) {
					field.setValue(entity, value2);
				}
				value = value2;
			}
		}
		return value;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getMappingFieldValue(T entity, FieldInfo field)  {
		FieldMapping mapping = field.getMapping();
		FieldInfo fkField = em.getField(mapping.getForeignKeyField());
		Object relKeyId = getFieldValue(entity, fkField.getName());
		EntityAccessObject relEao = Eaos.getEao(fkField.getMasterClass());
		if (relEao!=null) {
			Entity relEntity = relEao.getById(relKeyId);
			Object relValue = relEao.getFieldValue(relEntity, mapping.getMasterField());
			return relValue;
		}
		else {
			System.out.println("关联EAO未建立");
			return null;
		}
	}

	public void delete(String where, Object[] values, boolean checkForeignUse) {
		TabQuery query = new TabQuery(em);
		query.addWhereItem(where, values);
		queryManager.clear();
		List<String> ids = queryManager.queryIdList(query);
		List<T> list = query(query);
		if (checkForeignUse) {
			for (String id: ids) {
				testForienUseBeforeDelete(id);
			}
		}
		dao.delete(query);
		afterChange(ChangeFactory.newDeleteBatch(list));
	}
	
	public void batchInsert(List<T> list) {
		if (list!=null) {
			for (T entity:list) {
				this.insert(entity);
			}
		}
	}
	
	private void afterChange(DataChange change) {
		TransactionManager.afterChange(change, dataChangeListeners);
	}
	
	public void setRelationValues(List<Record> list, Class<? extends Relation> clazz, String id2) {
		
		RelationModel relModel = TableModels.getByClass(clazz);
		FieldInfo[] keyFields = relModel.getKeyFields(entityClass);

		List<String> cols = new ArrayList<>();
		for (FieldInfo field: relModel.getFields()) {
			if (!field.isForeignKey() && field.columnExists()) {
				cols.add(field.getColumnName());
			}
		}
		String colStr = StringUtils.link(cols, ", ");
		colStr = keyFields[0].getColumnName()+" id, "+colStr;
		
		String sql = "select "+colStr+" from "+relModel.getFullTableName()
			+" where "+keyFields[1].getColumnName()+"=?";
		
		List<ListOrderedMap> listExt = dao.queryForList(sql, id2);
		
		Object id;
		ListOrderedMap extRec;
		for (Record rec: list) {
			id = rec.get("id");
			extRec = findRecordById(listExt, id);
			if (extRec!=null) {
				extRec.remove("id");
				setRelationValues(rec, extRec, relModel);
			}
		}

	}
	
	public void setRelationValues(Record record, Class<? extends Relation> clazz, String id2) {
		
		final RelationModel relModel = TableModels.getByClass(clazz);
		
		List<String> cols = new ArrayList<>();
		for (FieldInfo field: relModel.getFields()) {
			if (!field.isForeignKey() && field.columnExists()) {
				cols.add(field.getColumnName());
			}
		}
		String colStr = StringUtils.link(cols, ", ");
		
		FieldInfo[] keyFields = relModel.getKeyFields(entityClass);
		String where = keyFields[0].getColumnName()+"=? and "+keyFields[1].getColumnName()+"=?";
		
		String sql = "select "+colStr+" from "+relModel.getFullTableName()
			+" where "+where;
		
		String id1 = record.getString("id");
		
		List<ListOrderedMap> listExt = dao.queryForList(sql, id1, id2);
		
		if (listExt.size()>0) {
			ListOrderedMap extRec = listExt.get(0);
			MyLogger.printMap(extRec, "extRec");
			setRelationValues(record, extRec, relModel);
		}
		
	}
	
	
	
	@SuppressWarnings("rawtypes")
	private <R extends Map> R findRecordById(List<R> list, Object id) {
		if (id==null) {
			return null;
		}
		String idStr = id.toString();
		Object id2;
		for (R rec: list) {
			id2 = rec.get("id");
			if (id2!=null && id2.toString().equals(idStr)) {
				return rec;
			}
		}
		return null;
	}	
	
	@SuppressWarnings("rawtypes")
	protected void setRelationValues(Record record, Map extRec, RelationModel relModel) {
		if (record==null || extRec==null || relModel==null) {
			return;
		}
		Object value;
		FieldInfo field;
		Record relRec = new Record();
		for (Object name: extRec.keySet()) {
			field = relModel.getField(name.toString());
			value = extRec.get(name);
			if (field!=null && value!=null) {
				if (value instanceof Boolean) {
					value = ((Boolean)value).booleanValue()?1:0;
				}
				relRec.put(field.getName(), value);
			}
		}
		record.put(relModel.getFieldPrefix(), relRec);
	}
	
	public boolean relationExists(Relation rel) {
		RelationModel relModel = TableModels.getByClass(rel.getClass());
		FieldInfo[] keyFields = relModel.getKeyFields(entityClass);
		String where = keyFields[0].getColumnName()+"=? and "+keyFields[1].getColumnName()+"=?";
		Object[] ids = new Object[]{ keyFields[0].getValue(rel), keyFields[1].getValue(rel)};
		int count = dao.queryCount(relModel.getFullTableName(), where, ids);
		return count>0;
	}
	
	public boolean insertRelation(Relation rel){
		if (rel==null) {
			return false;
		}
		if (relationExists(rel)) {
			return false;
		}
		RelationModel relModel = TableModels.getByClass(rel.getClass());
		rel.setCreateTime(new Date());
		insert(rel, relModel);
		return true;
	}
	
}
