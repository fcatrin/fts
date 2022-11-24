package fts.events;

import fts.core.Widget;

public class TouchEvent {
	public enum Button {LEFT, MIDDLE, RIGHT};
	public enum Action {UP, DOWN, MOVE};
	
	public Action action;
	public Button button = Button.LEFT;
	
	public int x;
	public int y;
	public Widget widget;
	public long timestamp;
	
	@Override
	public String toString() {
		return String.format("{TouchEvent action:%s %d,%d}", action.name(), x, y);
	}
}
