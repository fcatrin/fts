package fts.core;

import java.util.List;

public abstract class ListAdapter<T> {
	protected List<T> items;

	public void setItems(List<T> items) {
		this.items = items;
	}
	
	public int getCount() {
		return items == null ? 0 : items.size();
	}
	
	public T getItem(int index) {
		return items == null ? null : items.get(index);
	}
	
	public abstract Widget getWidget(Widget widget, int index, Widget list);
}
