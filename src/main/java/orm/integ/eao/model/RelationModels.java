package orm.integ.eao.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RelationModels {

	private static final Map<String, RelationModel> rms = new HashMap<>();

	public static RelationModel getByClass(Class<? extends Relation> clazz) {
		return rms.get(clazz.getName());
	}

	public static void putModel(RelationModel model) {
		if (model!=null) {
			rms.put(model.getRelationClass().getName(), model);
		}
	}

	public static Collection<RelationModel> getRelationModels() {
		return rms.values();
	}

}
