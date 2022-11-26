package fts.gl;

import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

import fts.core.Utils;
import fts.ui.CoreComponentFactory;
import fts.ui.Resources;
import fts.ui.Widget;
import fts.ui.graphics.BackBuffer;
import fts.ui.graphics.Drawable;
import fts.ui.graphics.Image;

public abstract class GLFactory extends CoreComponentFactory {

	@Override
	public Widget createWidget(Element element) {
		return super.createWidget(element);
	}

	@Override
	public Drawable createDrawable(Element element) {
		return super.createDrawable(element);
	}

	@Override
	public BackBuffer createBackBuffer(String id, int width, int height) {
		GLBackBuffer glBackBuffer = new GLBackBuffer();
		glBackBuffer.create(id, width, height);
		return glBackBuffer;
	}

	@Override
	public Image createImage(String src) {
		File imageFile;
		String name;
		if (src.startsWith("@images/")) {
			name = src;
			String fileName = src.substring("@images/".length());
			String location = "resources/images/" + fileName;
			
			if (!Resources.resourceExists(location)) {
				throw new RuntimeException("Image not found on " + location);
			}
			
			imageFile = Resources.resourceExtract(location);
			
		} else {
			imageFile = new File(src);
			name = imageFile.getAbsolutePath();
		}
		
		try {
			byte data[] = Utils.loadBytes(imageFile);
			return createImage(name, data);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load image from " + imageFile.getAbsolutePath(), e);
		}
	}

	@Override
	public Image createImage(String name, byte[] data) {
		return new GLImage(name, data);
	}
}
