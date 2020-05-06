package fts.events;

public class MouseEvent {
	public enum Button {LEFT, MIDDLE, RIGHT};
	public enum Action {UP, DOWN, MOVE};
	public int x;
	public int y;
	public Button button;
	public long timestamp;
}
