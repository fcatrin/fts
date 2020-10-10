package fts.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import fts.core.xml.ParserException;
import fts.core.xml.SimpleXML;
import fts.graphics.BackBuffer;
import fts.graphics.Color;
import fts.graphics.Drawable;

public class Application {
	static ComponentFactory factory;
	static ResourceLocator resourceLocator;
	static Context context;
	static Logger logger;
	
	private Application() {};
	
	public static void init(ComponentFactory factory, ResourceLocator resourceLocator, Logger logger, Context context) {
		Application.factory = factory;
		Application.context = context;
		Application.resourceLocator = resourceLocator;
		Application.logger = logger;
		
		loadFonts();
		loadColors();
		loadStrings();
		loadDimen();
	}
	
	public static NativeWindow createNativeWindow(String title, int width, int height, int flags) {
		return factory.createNativeWindow(title, width, height, flags);
	}
	
	public static Drawable createDrawable(Element node) {
		return factory.createDrawable(node);
	}
	
	public static BackBuffer createBackBuffer(int width, int height) {
		return factory.createBackBuffer(width, height);
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
			if (!resourceLocator.exists(location)) {
				throw new RuntimeException("Font " + name + " not found on " + location);
			}
			
			try {
				File fontFile = resourceLocator.extract(location);
				
				factory.registerFont(name, fontFile);
				if (!defaultHasBeenRegistered) {
					factory.registerFont("default", fontFile);
					defaultHasBeenRegistered = true;
				}
			} catch (IOException e) {
				throw new RuntimeException("Cannot extract font " + location, e);
			}
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
			factory.registerColor(name, color);
		}
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

	protected static Widget createWidget(NativeWindow w, Element node) {
		String name = node.getNodeName();
		
		if (name.equals("include")) {
			String includeName = node.getAttribute("layout");
			if (Utils.isEmptyString(includeName)) throw new RuntimeException("Include: Missing layout name " + node);
			return inflate(w, includeName);
		}
		
		Widget widget = factory.createWidget(node);
		
		if (widget == null) {
			widget = (Widget)createComponentInstance(w, name);
		}
		
		if (widget == null) {
			String viewClassName = "fts.widgets." + name;
			widget = (Widget)createComponentInstance(w, viewClassName);
		}
		
		if (widget == null) {
			throw new RuntimeException("Cannot create widget " + name);
		}
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
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
	public static Component createComponentInstance(NativeWindow w, String className) {
		Class componentClass;
		try {
			componentClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		try {
			return (Component)componentClass.getDeclaredConstructor(NativeWindow.class).newInstance(w);
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

	public static Drawable loadDrawable(String name) {
		Document doc = loadResource("drawable", name);
		Element root = doc.getDocumentElement();
		return createDrawable(root);
	}
	
	public static Widget inflate(NativeWindow w, String name) {
		Document doc = loadResource("layout", name);
		Element root = doc.getDocumentElement();
		return createWidget(w, root);
	}
}
