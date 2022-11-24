package fts.ui.events;

import fts.ui.widgets.ListWidget;

public interface OnItemSelectionChangedListener<T> {
	public void onItemSelectionChanged(ListWidget<T> widget, T item, int index);
}
