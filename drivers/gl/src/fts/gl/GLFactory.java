package fts.gl;

import org.w3c.dom.Element;

import fts.core.CoreComponentFactory;
import fts.core.NativeView;
import fts.core.Widget;
import fts.core.Window;
import fts.graphics.BackBuffer;
import fts.graphics.Drawable;

public abstract class GLFactory extends CoreComponentFactory {

	@Override
	public Widget createWidget(Element element) {
		return super.createWidget(element);
	}

	@Override
	public NativeView createNativeView(Window w) {
		return null;
	}

	@Override
	public Drawable createDrawable(Element element) {
		return super.createDrawable(element);
	}

	@Override
	public BackBuffer createBackBuffer(int width, int height) {
		GLBackBuffer glBackBuffer = new GLBackBuffer();
		glBackBuffer.create(width, height);
		return glBackBuffer;
	}

}
