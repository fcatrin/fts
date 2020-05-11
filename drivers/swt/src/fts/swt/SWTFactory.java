package fts.swt;

import java.io.File;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fts.core.ComponentFactory;
import fts.core.NativeView;
import fts.core.Widget;
import fts.core.Window;
import fts.graphics.Drawable;

public class SWTFactory implements ComponentFactory {
	public Window createWindow() {
		return new SWTWindow();
	}

	@Override
	public NativeView createNativeView(Window w) {
		return new SWTNativeView(((SWTWindow)w).shell);
	}

	@Override
	public Widget createWidget(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable createDrawable(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerFont(String alias, File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public File getFont(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllFontAliases() {
		// TODO Auto-generated method stub
		return null;
	}
}
