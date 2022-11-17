package fts.utils.dialogs;

public abstract class DialogInputCallback extends DialogCallback {
	public final void onYes() {}
	public abstract void onInput(String text);
}
