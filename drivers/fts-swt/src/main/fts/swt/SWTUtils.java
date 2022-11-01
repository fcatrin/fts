package fts.swt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fts.core.Utils;

public class SWTUtils {
	public static Display display;
	private static Set<String> knownFonts = new HashSet<String>();
	private static Map<String, Font> loadedFonts = new HashMap<String, Font>();
	
	private SWTUtils() {}
	
	public static void setFillHorizontal(Control c) {
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	public static void setFillVertical(Control c) {
		c.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}
	public static void setFillBoth(Control c) {
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public static void setAlignHorizontal(Control c, int align) {
	    GridData layoutData = (GridData)c.getLayoutData();
	    if (layoutData == null) layoutData = new GridData();
	    layoutData.horizontalAlignment = align;
	    c.setLayoutData(layoutData);
	}
	
	public static void setSize(Control c, int w, int h) {
	    GridData layoutData = (GridData)c.getLayoutData();
	    if (layoutData == null) layoutData = new GridData();
	    layoutData.widthHint = w;
	    layoutData.heightHint = h;
	    c.setLayoutData(layoutData);
	}
	
	public static void setWidth(Control c, int w) {
	    GridData layoutData = (GridData)c.getLayoutData();
	    if (layoutData == null) layoutData = new GridData();
	    layoutData.widthHint = w;
	    c.setLayoutData(layoutData);
	}

	public static void setHeight(Control c, int h) {
	    GridData layoutData = (GridData)c.getLayoutData();
	    if (layoutData == null) layoutData = new GridData();
	    layoutData.heightHint = h;
	    c.setLayoutData(layoutData);
	}

	
	public static Shell findShell(Control c) {
		if (c instanceof Shell) return (Shell)c;
		
		Control parent = c.getParent();
		if (parent == null) return null;
		
		return findShell(parent);
	}
	
	public static void enableAll(Composite parent, boolean enabled) {
		for(Control c : parent.getChildren()) {
			if (c instanceof Composite) enableAll((Composite)c, enabled);
			else c.setEnabled(enabled);
		}
		parent.setEnabled(enabled);
	}

	public static void showMessage(Control c, String title, String message) {
		Shell shell = findShell(c);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText(title);
		dialog.setMessage(message);
		dialog.open();
	}
	
	public static boolean showQuestion(Control c, String title, String message) {
		Shell shell = findShell(c);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
		dialog.setText(title);
		dialog.setMessage(message);
		return dialog.open() == SWT.OK;
	}

	public static Text createEditor(Composite parent, String label) {
		return createEditor(parent, label, false);
	}
	
	public static Text createEditor(Composite parent, String label, boolean isPassword) {
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setText(label);
		Text txtEditor = new Text(parent, (isPassword?SWT.PASSWORD:SWT.SINGLE) | SWT.BORDER);
		SWTUtils.setFillHorizontal(txtEditor);
		return txtEditor;
	}
	
	public static Label createLabel(Composite parent, String label) {
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(label);
		return lbl;
	}
	
	public static Image loadImage(Display display, String path) throws IOException {
		InputStream is = SWTUtils.class.getClassLoader().getResourceAsStream(path);
		if (is == null) {
			is = SWTUtils.class.getClassLoader().getResourceAsStream("xtvapps/core/swt/" + path);
		}
		if (is!=null) {
			Image image = new Image(display, is);
			is.close();
			return image;
		} else {
			System.out.println("image resource not found: " + path);
		}
		return null;
	}
	
	public static Image[] getIcons(Display display, ClassLoader classLoader, String basePath) throws IOException {
		int sizes[] = {16, 32, 64, 128};
		Map<Integer, Image> icons = new HashMap<Integer, Image>();
		
		// first try from resource
		for(int size: sizes) {
			String path = basePath.replace("{size}", size+"");
			Image image = loadImage(display, path);
			if (image!=null) icons.put(size, image);
		}

		Image finalIcons[] = new Image[icons.size()];
		int i = 0;
		for(Image icon : icons.values()) {
			finalIcons[i++] = icon;
		}
		return finalIcons;
	}

	public static void centerOnScreen(Shell shell) {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
	}

	public static void centerOnParent(Shell shell) {
		Rectangle bounds = shell.getParent().getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
	}

	public static void mainLoop(Shell shell) {
		while (!shell.isDisposed()) {
			try {
				if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	public static Image buildImage(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		Image image = null;
		try {
			image = new Image(display, bais);
		} finally {
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	
	public static Image resizeImage(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose(); 
		return scaled;
	}
	
	private static void loadFont(String fontName) {
		if (knownFonts.contains(fontName)) return;
		knownFonts.add(fontName);

		File fontsDir = new File(System.getProperty("java.io.tmpdir") + "/xtvapps.swt");
		if (!fontsDir.exists()) fontsDir.mkdirs();

		InputStream is = SWTUtils.class.getClassLoader().getResourceAsStream("res/fonts/" + fontName + ".ttf");
		if (is == null) {
			is = SWTUtils.class.getClassLoader().getResourceAsStream("xtvapps/core/swt/res/fonts/" + fontName);
		}
		
		if (is == null) return;
		
		FileOutputStream os;
		try {
			File fontFile = new File(fontsDir, fontName + ".ttf");
			os = new FileOutputStream(fontFile);
			Utils.copyFile(is, os);
			
			Display display = Display.getCurrent();
			display.loadFont(fontFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Font buildFont(String fontName, int size, int style) {
		String key = fontName + "." + size + "." + style;
		Font font = loadedFonts.get(key);
		if (font == null) {
			loadFont(fontName);
			font = new Font(display, new FontData(fontName, size, style));
			loadedFonts.put(key, font);
		}
		return font;
	}
	
	public static void addSpacerHorizontal(Composite parent) {
		setFillHorizontal(new Label(parent, SWT.NONE));
	}
	
	public static void addSpacerVertical(Composite parent) {
		setFillVertical(new Label(parent, SWT.NONE));
	}
	
	public static String ellipsize(GC gc, int maxWidth, String text) {
		String s = text;
		do {
			int extent = gc.stringExtent(s).x;
			if (extent<= maxWidth) return s;
			text = text.substring(0, text.length()-2).trim();
			s = text + "...";
		} while (text.length()>0);
		return "";
	}

}
