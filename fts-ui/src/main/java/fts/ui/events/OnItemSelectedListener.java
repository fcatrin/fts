package fts.ui.events;

import fts.ui.widgets.ListWidget;

public interface OnItemSelectedListener<T> {
	public void onItemSelected(ListWidget<T> widget, T item, int index);
}
