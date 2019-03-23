package orm.integ.eao.model;

import java.util.ArrayList;
import java.util.List;

import orm.integ.dao.DataAccessObject;
import orm.integ.utils.IntegError;

@SuppressWarnings("rawtypes")
public class RelationModelBuilder extends TableModelBuilder {

	private String[] keyColumns;
	private FieldInfo[] keyFields;
	private String fieldPrefix;
	
	public RelationModelBuilder(Class relationClass, DataAccessObject dao) {
		super(relationClass, dao);
		
		FieldInfo fi;
		List<String> keyColumnList = new ArrayList<>();
		List<FieldInfo> keyFields = new ArrayList<>();
		
		for (String field:normalFields) {
			fi = this.getField(field);
			if (fi.isForeignKey() && fi.columnExists()) {
				keyFields.add(fi);
				keyColumnList.add(fi.getColumnName());
			}
		}
		if (keyColumnList.size()!=2) {
			String name = relationClass.getSimpleName();
			throw new IntegError("关系类"+name+"配置错误，外键数应为2个");
		}
		if (keyFields.get(0).getMasterClass()==keyFields.get(1).getMasterClass()) {
			throw new IntegError("关系类"+relationClass.getName()+"的两个外键字段对应的主类不能相同");
		}
		this.keyColumns = keyColumnList.toArray(new String[0]);
		this.keyFields = keyFields.toArray(new FieldInfo[0]);
		
		String temp = relationClass.getSimpleName();
		fieldPrefix = temp.substring(0, 1).toLowerCase()+temp.substring(1);
		
	}

	public RelationModel buildModel() {
		RelationModel model = new RelationModel();
		this.buildModel(model);
		model.keyColumns = keyColumns;
		model.keyFields = keyFields;
		model.fieldPrefix = fieldPrefix;
		TableModels.putModel(model);
		return model;
	}

}
