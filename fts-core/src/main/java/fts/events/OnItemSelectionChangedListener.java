package fts.events;

import fts.widgets.ListWidget;

public interface OnItemSelectionChangedListener<T> {
	public void onItemSelectionChanged(ListWidget<T> widget, T item, int index);
}
