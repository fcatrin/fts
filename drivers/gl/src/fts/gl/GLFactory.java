package fts.gl;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Element;

import fts.core.Application;
import fts.core.CoreComponentFactory;
import fts.core.Utils;
import fts.core.Widget;
import fts.graphics.BackBuffer;
import fts.graphics.Drawable;
import fts.graphics.Image;

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
	public BackBuffer createBackBuffer(int width, int height) {
		GLBackBuffer glBackBuffer = new GLBackBuffer();
		glBackBuffer.create(width, height);
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
			
			if (!Application.resourceExists(location)) {
				throw new RuntimeException("Image not found on " + location);
			}
			
			imageFile = Application.resourceExtract(location);
			
		} else {
			imageFile = new File(src);
			name = imageFile.getAbsolutePath();
		}
		
		try {
			byte data[] = Utils.loadBytes(imageFile);
			return new GLImage(name, data);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load image from " + imageFile.getAbsolutePath(), e);
		}
	}

}
