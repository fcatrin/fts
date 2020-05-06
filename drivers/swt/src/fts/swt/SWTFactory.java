package fts.swt;

import org.w3c.dom.Node;

import fts.core.ComponentFactory;
import fts.core.NativeView;
import fts.core.Window;
import fts.widgets.Widget;

public class SWTFactory implements ComponentFactory {
	public Window createWindow() {
		return new SWTWindow();
	}

	@Override
	public Widget createView(Node node) {
		return null;
	}

	@Override
	public NativeView createNativeView(Window w) {
		return new SWTNativeView(((SWTWindow)w).shell);
	}
}
