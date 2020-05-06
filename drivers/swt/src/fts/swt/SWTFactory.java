package fts.swt;

import org.w3c.dom.Node;

import fts.core.ComponentFactory;
import fts.core.NativeView;
import fts.core.Window;
import fts.views.View;

public class SWTFactory implements ComponentFactory {
	public Window createWindow() {
		return new SWTWindow();
	}

	@Override
	public View createView(Node node) {
		return null;
	}

	@Override
	public NativeView createNativeView(Window w) {
		return new SWTNativeView(((SWTWindow)w).shell);
	}
}
