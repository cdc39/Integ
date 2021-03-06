package orm.integ.eao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.PageRequest;
import orm.integ.dao.sql.QueryRequest;
import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityModel;
import orm.integ.eao.model.FieldInfo;
import orm.integ.utils.Convertor;
import orm.integ.utils.PageData;
import orm.integ.utils.Record;

public abstract class QueryHandler {

	@SuppressWarnings("rawtypes")
	EntityAccessObject eao ;
	DataAccessObject dao;
	EntityModel model;
	RecordExtender extender;
	QueryRequest query;
	
	@SuppressWarnings("rawtypes")
	QueryHandler(EntityAccessObject eao) {
		this.eao = eao;
		dao = eao.getDAO();
		model = eao.getEntityModel();
	}
	
	protected void setQueryRequest(QueryRequest query) {
		this.query = query;
	}

	
	public QueryHandler setPageInfo(int start, int limit) {
		query.setPageInfo(start, limit);
		return this;
	}
	
	public QueryHandler setPageInfo(PageRequest req) {
		query.setPageInfo(req.getStart(), req.getLimit());
		return this;
	}
	
	public QueryHandler setFields(String... fields) {
		query.setViewFields(fields);
		return this;
	}
	
	public QueryHandler setRecordExtender(RecordExtender extender) {
		this.extender = extender;
		return this;
	}


	@SuppressWarnings("unchecked")
	public <E extends Entity> E first() {
		return (E) eao.queryFirst(query);
	}
	
	public int count() {
		return eao.queryCount(query); 
	}
	
	@SuppressWarnings("rawtypes")
	public List list() {
		return eao.query(query);
	}
	
	public PageData page() {
		PageData page = eao.pageQuery(query);
		fillExtendValues(page.getList());
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> idList() {
		List<Entity> list = eao.query(query);
		List<String> ids = new ArrayList<>();
		for (Entity en: list) {
			ids.add(en.getId());
		}
		return ids;
	}
	
	public Set<String> idSet(String fieldName) {
		List<String> idList = stringList(fieldName);
		Set<String> idSet = new LinkedHashSet<>();
		idSet.addAll(idList);
		return idSet;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> stringList(String fieldName) {
		List<Entity> list = eao.query(query);
		List<String> valueList = new ArrayList<>();
		FieldInfo field = eao.getEntityModel().getField(fieldName);
		if (field!=null) {
			Object val;
			String strVal;
			for (Entity en: list) {
				val = field.getValue(en);
				if (val!=null) {
					strVal = Convertor.toString(val);
					valueList.add(strVal);
				}
			}
		}
		return valueList;
	}
	
	protected void fillExtendValues(List<Record> recs) {
		if (extender!=null) {
			List<Record> removes = new ArrayList<>();
			for (Record r: recs) { 
				try {
					boolean rt = extender.fillRecordExt(r);
					if (!rt) {
						removes.add(r);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			recs.removeAll(removes);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> recordList(){
		List list = list();
		List<Record> recList = eao.toRecords(list);
		fillExtendValues(recList);
		return recList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void forEach(EntityHandler handler) {
		List list = this.list();
		for (Object obj: list) {
			handler.handle((Entity)obj, eao);
		}
	}
	
	public boolean exists() {
		return this.count()>0;
	}
	
	
}


