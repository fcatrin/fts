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
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Packager {

	private File destinationDir;
	private File projectDir;

	Properties properties = new Properties();
	
	public Packager(File projectDir, File destinationDir) throws FileNotFoundException, IOException {
		this.projectDir = projectDir;
		this.destinationDir = destinationDir;
		
		File propertiesFile = new File(projectDir, "fts.properties");
		if (!propertiesFile.exists()) {
			System.out.println("FTS properties not found: " + propertiesFile.getCanonicalPath());
			return;
		}
		properties.load(new FileInputStream(propertiesFile));
		
	}

	protected void buildPackage(List<String> deps) throws FileNotFoundException, IOException {
		List<File> libraryDirs = getLibraryDirs(); 
		for(File libraryDir : libraryDirs) {
			Packager packager = new Packager(libraryDir, destinationDir);
			packager.buildPackage(deps);
		}
		
		String packageName = getPackageName();
		String packageFileName = packageName + "-resources.jar";
		
		deps.add(packageName);
		
		File resourcesFile = new File(destinationDir, packageFileName);
		System.out.println("Creating " + resourcesFile.getCanonicalPath());
		buildZip(resourcesFile, packageName);
	}
	
	private void buildZip(File resourcesFile, String packageName) throws IOException {
		FileOutputStream fos = new FileOutputStream(resourcesFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        
        String packagePath = packageName.replace(".", "/");
        addFiles(zos, packagePath, "resources");
        zos.close();
	}
	
	private void addFiles(ZipOutputStream zos, String packagePath, String path) throws IOException {
		File dir = new File(projectDir, path);
		File files[] = dir.listFiles();
		
		if (files == null) return;
		
		for(File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				addFiles(zos, packagePath, path + "/" + fileName);
			} else {
				zos.putNextEntry(new ZipEntry(packagePath + "/" + path + "/" + fileName));
		        byte[] bytes = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
		        zos.write(bytes, 0, bytes.length);
			}
	        zos.closeEntry();
		}
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
		File depsFile = new File(destinationDir, "deps.properties");
		FileWriter fw = new FileWriter(depsFile);
		for(String dep : deps) {
			fw.append(dep);
			fw.append("\n");
		}
		fw.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File curDir = new File(".");
		File dstDir = new File(curDir, "bin");
		
		if (!dstDir.exists()) {
			System.out.println("Directory not found: " + dstDir.getCanonicalPath());
			return;
		}

		List<String> deps = new ArrayList<String>();
		Packager packager = new Packager(new File("."), dstDir);
		packager.buildPackage(deps);
		packager.saveDeps(deps);
	}

}
