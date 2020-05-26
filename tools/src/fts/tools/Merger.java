package fts.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Merger {
	private static final String GEN_DIR_NAME = "fts";
	
	private File destinationDir;
	private File projectDir;

	Properties properties = new Properties();
	
	public Merger(File projectDir, File destinationDir) throws FileNotFoundException, IOException {
		this.projectDir = projectDir;
		this.destinationDir = destinationDir;
		
		File propertiesFile = new File(projectDir, "fts.properties");
		if (!propertiesFile.exists()) {
			System.out.println("FTS properties not found: " + propertiesFile.getCanonicalPath());
			return;
		}
		properties.load(new FileInputStream(propertiesFile));
		
	}

	protected void buildPackage(List<String> deps) throws FileNotFoundException, IOException, ParserException, TransformerFactoryConfigurationError, TransformerException {
		List<File> libraryDirs = getLibraryDirs(); 
		for(File libraryDir : libraryDirs) {
			Merger packager = new Merger(libraryDir, destinationDir);
			packager.buildPackage(deps);
		}
		
		String packageName = getPackageName();
		deps.add(packageName);
		
		System.out.println("process " + projectDir.getCanonicalPath());
        mergeFiles(GEN_DIR_NAME, "resources");
	}
	
	private void mergeFiles(String packagePath, String path) throws IOException, ParserException, TransformerFactoryConfigurationError, TransformerException {
		File dir = new File(projectDir, path);
		File files[] = dir.listFiles();
		
		if (files == null) return;
		
		for(File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				mergeFiles(packagePath, path + "/" + fileName);
			} else {
				File dstFile = new File(destinationDir, packagePath + "/" + path + "/" + file.getName());
				if (path.contains("/values") && dstFile.exists()) {
					System.out.println("merge " + dstFile.getCanonicalPath());
					mergeFile(packagePath, path, file, dstFile);
				} else {
					System.out.println("create " + dstFile.getCanonicalPath());
					dstFile.getParentFile().mkdirs();
					ToolsUtils.copyFile(file, dstFile);
				}
			}
		}
	}
	
	private void mergeFile(String packagePath, String path, File srcFile, File dstFile) throws ParserException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Document oldDoc = SimpleXML.parse(srcFile);
		Document newDoc = SimpleXML.parse(dstFile);
		
		Element oldRoot = oldDoc.getDocumentElement();
		Element newRoot = newDoc.getDocumentElement();
		
		List<Element> elements = SimpleXML.getElements(newRoot);
		for(Element element : elements) {
			String nodeName = element.getNodeName();
			String name = SimpleXML.getAttribute(element, "name");
			String value = SimpleXML.getText(element);

			boolean found = false;
			NodeList childNodes = oldRoot.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals(nodeName) && name.equals(SimpleXML.getAttribute(node, "name"))) {
					node.setNodeValue(value);
					found = true;
					break;
				}
			}
			
			if (!found) {
				Element newElement = oldDoc.createElement(nodeName);
				newElement.setAttribute("name", name);
				newElement.setTextContent(value);
				
				oldRoot.appendChild(newElement);
			}
		}
		
		SimpleXML.write(oldDoc, new FileOutputStream(dstFile));
	}

	protected List<File> getLibraryDirs() throws IOException {
		List<File> libraryDirs = new ArrayList<File>();
		for(int i=1; i<100; i++) {
			String libraryDirName = properties.getProperty("libs." + i);
			if (libraryDirName == null) break;
			
			File libraryDir = new File(libraryDirName);
			libraryDirs.add(libraryDir);
		}
		return libraryDirs;
	}
	
	protected String getPackageName() {
		return properties.getProperty("package");
	}

	private void saveDeps(List<String> deps) throws IOException {
		File depsFile = new File(destinationDir, "ftsdeps.properties");
		FileWriter fw = new FileWriter(depsFile);
		for(String dep : deps) {
			fw.append(dep);
			fw.append("\n");
		}
		fw.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParserException, TransformerFactoryConfigurationError, TransformerException {
		File curDir = new File(".");
		File dstDir = new File(curDir, "bin");
		
		if (!dstDir.exists()) {
			System.out.println("Directory not found: " + dstDir.getCanonicalPath());
			return;
		}
		
		ToolsUtils.delTree(new File(dstDir, GEN_DIR_NAME));

		List<String> deps = new ArrayList<String>();
		Merger packager = new Merger(curDir, dstDir);
		packager.buildPackage(deps);
		packager.saveDeps(deps);
	}

}
