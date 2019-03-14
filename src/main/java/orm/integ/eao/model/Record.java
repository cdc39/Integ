package orm.integ.eao.model;

import java.util.HashMap;

public class Record extends HashMap<String, Object> {

	private static final long serialVersionUID = -1499141895993079047L;

	public String getString(String name) {
		Object value = this.get(name);
		return value==null?null:value.toString();
	}
	
}
