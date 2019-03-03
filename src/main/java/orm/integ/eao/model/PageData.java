package orm.integ.eao.model;

import java.util.List;

public class PageData {

	private final List<Record> list;
	
	private final int totalCount;

	public PageData(List<Record> list, int totalCount) {
		this.list = list;
		this.totalCount = totalCount;
	}
	
	public List<Record> getList() {
		return list;
	}

	public int getTotalCount() {
		return totalCount;
	}

	
}
