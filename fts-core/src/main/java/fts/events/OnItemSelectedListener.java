package fts.events;

import fts.widgets.ListWidget;

public interface OnItemSelectedListener<T> {
	public void onItemSelected(ListWidget<T> widget, T item, int index);
}
