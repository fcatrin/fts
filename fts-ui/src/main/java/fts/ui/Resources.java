package fts.ui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fts.core.CoreUtils;
import fts.core.ResourceLocator;
import fts.core.xml.ParserException;
import fts.core.xml.SimpleXML;
import fts.ui.graphics.BackBuffer;
import fts.ui.graphics.Color;
import fts.ui.graphics.ColorListSelector;
import fts.ui.graphics.Dimension;
import fts.ui.graphics.Drawable;
import fts.ui.graphics.Image;

public class Resources {
	public static ComponentFactory factory;
	static ResourceLocator resourceLocator;

	private Resources() {};
	
	public static void init(ComponentFactory factory, ResourceLocator resourceLocator) {
		Resources.factory = factory;
		Resources.resourceLocator = resourceLocator;

		loadFonts();
		loadColors();
		loadStrings();
		loadDimen();
		loadStyles();
	}
	
	public static Window createNativeWindow(String title, int width, int height, int flags) {
		return factory.createNativeWindow(title, width, height, flags);
	}
	
	public static Drawable createDrawable(Element node) {
		return factory.createDrawable(node);
	}
	
	public static BackBuffer createBackBuffer(String id, int width, int height) {
		return factory.createBackBuffer(id, width, height);
	}

	public static Image createImage(String src) {
		return factory.createImage(src);
	}

	public static Image createImage(String name, byte data[]) {
		return factory.createImage(name, data);
	}

	public static ComponentFactory getFactory() {
		return factory;
	}
	
	public static void loadFonts() {
		Document fontResources;
		try  {
			fontResources = loadResource("values", "fonts");
		} catch (Exception e) {
			return;
		}
		
		List<Element> fontDescriptors = SimpleXML.getElements(fontResources.getDocumentElement(), "font");
		boolean defaultHasBeenRegistered = false;
		for(Element fontDescriptor : fontDescriptors) {
			String name = fontDescriptor.getAttribute("name");
			String file = fontDescriptor.getTextContent();
			String location = "resources/fonts/" + file;
			if (!resourceExists(location)) {
				throw new RuntimeException("Font " + name + " not found on " + location);
			}
			
			File fontFile = resourceExtract(location);
			
			factory.registerFont(name, fontFile);
			if (!defaultHasBeenRegistered) {
				factory.registerFont("default", fontFile);
				defaultHasBeenRegistered = true;
			}
		}
	}
	
	public static boolean resourceExists(String path) {
		return resourceLocator.exists(path);
	}
	
	public static File resourceExtract(String path) {
		try {
			return resourceLocator.extract(path);
		} catch (IOException e) {
			throw new RuntimeException("Cannot extract resource " + path, e);
		}
	}

	public static void loadColors() {
		Document colorResources;
		try  {
			colorResources = loadResource("values", "colors");
		} catch (Exception e) {
			return;
		}
		
		List<Element> colorDescriptors = SimpleXML.getElements(colorResources.getDocumentElement(), "color");
		for(Element colorDescriptor : colorDescriptors) {
			String name = colorDescriptor.getAttribute("name");
			String value = colorDescriptor.getTextContent();
			Color color = new Color(value);
			factory.registerColor(name, new ColorListSelector(color));
		}
	}
	
	public static ColorListSelector loadColorResource(String name) {
		Document colorResource = loadResource("color", name);
		return new ColorListSelector(colorResource.getDocumentElement());
	}

	public static void loadStyles() {
		Document styleResources;
		try  {
			styleResources = loadResource("values", "styles");
		} catch (Exception e) {
			return;
		}
		
		List<Element> styleDescriptors = SimpleXML.getElements(styleResources.getDocumentElement(), "style");
		for(Element styleDescriptor : styleDescriptors) {
			String name = styleDescriptor.getAttribute("name");
			Style style = getExistingStyle(name);
			if (style == null) style = new Style(name);

			String parentName = SimpleXML.getAttribute(styleDescriptor, "parent"); 
			if (parentName != null) style.setParent(parentName);
			
			List<Element> attributeDescriptors = SimpleXML.getElements(styleDescriptor, "item");
			for(Element attributeDescriptor : attributeDescriptors) {
				String itemName = attributeDescriptor.getAttribute("name");
				String itemValue = attributeDescriptor.getTextContent();
				style.setAttribute(itemName, itemValue);
			}
			factory.registerStyle(name, style);
		}
	}
	
	private static Style getExistingStyle(String name) {
		try {
			return factory.getStyle(name);
		} catch (Exception e) {}
		return null;
	}

	public static void loadStrings() {
		Document stringResources;
		try  {
			stringResources = loadResource("values", "strings");
		} catch (Exception e) {
			return;
		}
		
		List<Element> stringDescriptors = SimpleXML.getElements(stringResources.getDocumentElement(), "string");
		for(Element stringDescriptor : stringDescriptors) {
			String name = stringDescriptor.getAttribute("name");
			String value = stringDescriptor.getTextContent();
			factory.registerString(name, value);
		}
	}

	public static void loadDimen() {
		Document stringResources;
		try  {
			stringResources = loadResource("values", "dimen");
		} catch (Exception e) {
			return;
		}
		
		List<Element> dimenDescriptors = SimpleXML.getElements(stringResources.getDocumentElement(), "dimen");
		for(Element dimenDescriptor : dimenDescriptors) {
			String name = dimenDescriptor.getAttribute("name");
			String value = dimenDescriptor.getTextContent();
			factory.registerDimen(name, value);
		}
	}

	protected static Widget createWidget(Window w, Element node) {
		String name = node.getNodeName();
		
		if (name.equals("include")) {
			String includeName = node.getAttribute("layout");
			if (CoreUtils.isEmptyString(includeName)) throw new RuntimeException("Include: Missing layout name " + node);
			return inflate(w, includeName);
		}
		
		Widget widget = factory.createWidget(node);
		
		if (widget == null) {
			widget = (Widget)createComponentInstance(w, name);
		}

		if (name.equals("Widget")) {
			widget = (Widget)createComponentInstance(w, "fts.ui.Widget");
		}

		if (widget == null) {
			String viewClassName = "fts.ui.widgets." + name;
			widget = (Widget)createComponentInstance(w, viewClassName);
		}
		
		if (widget == null) {
			throw new RuntimeException("Cannot create widget " + name);
		}
		
		// first set properties via styles
		String styleName = SimpleXML.getAttribute(node, "style");
		if (styleName!=null) {
			Style style = factory.getStyle(styleName);
			Map<String, String> attributes = style.getAttributes();
			for(Entry<String, String> attribute : attributes.entrySet()) {
				widget.setProperty(attribute.getKey(), attribute.getValue());
			}
		}
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
			String itemName = item.getNodeName();
			if (itemName.equals("style")) continue;
			
			widget.setProperty(item.getNodeName(), item.getNodeValue());
		}
		
		if (widget instanceof Container) {
			Container container = (Container)widget;
			List<Element> elements = SimpleXML.getElements(node);
			for(Element element : elements) {
				container.add(createWidget(w, element));
			}
		}
		
		return widget;
	}
			
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Component createComponentInstance(Window w, String className) {
		Class componentClass;
		try {
			componentClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		try {
			return (Component)componentClass.getDeclaredConstructor(Window.class).newInstance(w);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException("Missing constructor " + className +"(Window)");
		}
	}
	
	private static InputStream findResourceRaw(String type, String name) {
		String location = "resources/" + type + "/" + name;
		try {
			InputStream is = resourceLocator.getResource(location);
			if (is == null) {
				throw new RuntimeException("Resource not found " + location);
			}
			return is;
		} catch (IOException e) {
			throw new RuntimeException("Cannot load resource " + location, e);
		}
	}

	private static Document loadResource(String type, String name) {
		InputStream is = findResourceRaw(type, name + ".xml");

		try {
			 return SimpleXML.parse(is);
		} catch (ParserException e) {
			throw new RuntimeException("Cannot parse XML " + type + "/" + name, e);
		}
	}

	public static byte[] loadRawResource(String location) throws IOException {
		String type = "raw";
		String name = location;
		if (location.startsWith("@")) {
			location = location.substring(1);
			int p = location.indexOf("/");
			if (p>0) {
				type = location.substring(0, p);
				name = location.substring(p+1);
			}
		}
		return loadRawResource(type, name);
	}

	public static byte[] loadRawResource(String type, String name) throws IOException {
		InputStream is = findResourceRaw(type, name);
		if (is == null) {
			throw new RuntimeException("Resource not found @" + type + "/" + name);
		}
		return CoreUtils.loadBytes(is);
	}

	public static Drawable loadDrawable(String name) {
		Document doc = loadResource("drawable", name);
		Element root = doc.getDocumentElement();
		return createDrawable(root);
	}
	
	public static Widget inflate(Window w, String name) {
		Document doc = loadResource("layout", name);
		Element root = doc.getDocumentElement();
		return createWidget(w, root);
	}

	public static int getDimensionPixels(String dimension) {
		String value = dimension;
		if (dimension.startsWith("@dimen/")) {
			String alias = dimension.substring("@dimen/".length());
			String knownDimen = factory.getDimen(alias);
			if (knownDimen == null) {
				throw new RuntimeException("Unknown dimension " + dimension);
			}
			value = knownDimen;
		}
		try {
			return Dimension.parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid dimension " + value);
		}
	}
}
