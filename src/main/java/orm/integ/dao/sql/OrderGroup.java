package orm.integ.dao.sql;

import java.util.ArrayList;
import java.util.List;

import orm.integ.utils.StringUtils;

public class OrderGroup {

	public static OrderGroup parse(String orderStmt) {
		List<OrderItem> list = new ArrayList<>();
		if (orderStmt==null || orderStmt.trim().length()==0) {
			return new OrderGroup(list);
		}
		String[] items = orderStmt.split(",");
		OrderItem oi;
		for (String item: items) {
			oi = OrderItem.parseItem(item);
			if (oi!=null) {
				list.add(oi);
			}
		}
		return new OrderGroup(list);
	}
	
	public OrderGroup() {
	}
	
	private List<OrderItem> orderItems = new ArrayList<>();
	
	public OrderGroup(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
	public List<OrderItem> getItems() {
		return orderItems;
	}
	
	public boolean isEmpty() {
		return orderItems.size()==0;
	}
	
	public OrderGroup toReverse() {
		List<OrderItem> revItems = new ArrayList<>();
		for (OrderItem item: this.orderItems) {
			revItems.add(new OrderItem(item.getColumnName(), !item.isDesc()));
		}
		return new OrderGroup(revItems);
	}
	
	public String getColumnsString() {
		List<String> colNames = new ArrayList<>();
		for (OrderItem item: orderItems) {
			colNames.add(item.getColumnName());
		}
		return StringUtils.link(colNames, ", ");
	}
	
	public String toString() {
		if (orderItems.size()==0) {
			return " ";
		}
		List<String> strs = new ArrayList<>();
		for (OrderItem item: orderItems) {
			strs.add(item.toString());
		}
		String str = StringUtils.link(strs, ", ");
		return " order by "+str+" ";
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}
