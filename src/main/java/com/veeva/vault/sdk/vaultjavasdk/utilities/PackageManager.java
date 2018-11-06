package com.veeva.vault.sdk.vaultjavasdk.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;

import java.nio.file.StandardOpenOption;

public class PackageManager {
	
	private static Path PROJECT_DIRECTORY = Paths.get("");
	private static final String CURRENT_DATE = java.time.LocalDate.now().toString();
	private static int ZIP_FILE_INCREMENT = 1;
	private static String OUTPUT_FILE_PATH = "deployment/packages/";
	private static String OUTPUT_FILE_NAME = "code_package";
	private static OutputPackageFormat OUTPUT_ZIP_FILE = new OutputPackageFormat(OUTPUT_FILE_PATH);
	private static final String OUTPUT_XML_FILE = "deployment/vaultpackage.xml";
	private static long writtenBytesCount = 0;
	

	public static String getProjectPath() {
		return PROJECT_DIRECTORY.toAbsolutePath().toString();
	}
	
	public static String getSourcePath(String source) {
		Path path;
		final String sourcePath;
		
		if (source != null) {
			sourcePath = source.replaceAll("\\.", "/");
			path = Paths.get("", "javasdk/src/main/java/" + sourcePath);
		}
		else {
			sourcePath = "";
			path = Paths.get("", "javasdk/src/main/java/");
		}
		
		if (!Files.isDirectory(path)) {
			File dir = new File(path.getParent().toAbsolutePath().toString());
			for(String fileName : dir.list()) {
				if (fileName.toLowerCase().contains(path.getFileName().toString().toLowerCase())) {
					path = Paths.get(path.getParent().toString() + "/" + fileName);
				}
			}
		}
	
		System.out.println("Source Path: " + path.toAbsolutePath().toString());
		return path.toAbsolutePath().toString();
		
	}
	
	public static String getPackagePath() {
		String packagePath = null;
		if (Files.exists(Paths.get("", OUTPUT_ZIP_FILE.getString()))){
			packagePath = Paths.get("", OUTPUT_ZIP_FILE.getString()).toAbsolutePath().toString();
		}

		return packagePath;
		
	}
	
	public static boolean cleanPackageDirectory() {
		
		boolean deleted = false;
		try {
			Stream<Path> fileWalk = Files.walk(Paths.get("", "deployment"));
			List<Path> fileList = fileWalk.filter(pp -> !Files.isDirectory(pp)).collect(Collectors.toList());
			
			fileList.forEach(p -> {
					try {
						Files.deleteIfExists(p);
					} catch (IOException e) {
						System.out.println("ERROR " + e.toString()+"\n\n");
					}
			});
			fileWalk.close();

		} catch (IOException e) {
			System.out.println("ERROR " + e.toString()+"\n\n");
			return false;
		}
		return true;
	}
	
	//Create the required "vaultpackage.xml" file for the VPK. The specifies a default deployment option of "incremental".
	public static String createXMLFile(String username) {
		
		List<String> lines = Arrays.asList( "<vaultpackage xmlns=\"https://veevavault.com/\">",
											"<name>PKG-" + PROJECT_DIRECTORY.toAbsolutePath().getFileName().toString() + "-code</name>",
											"<source>",
											"<vault></vault>",
											"<author>"+ username + "</author>",
											"</source>",
											"<summary>The " + PROJECT_DIRECTORY.toAbsolutePath().getFileName().toString() + " project code.</summary>",
											"<description>This VPK contains the Vault Java SDK code for the '" + PROJECT_DIRECTORY.toAbsolutePath().getFileName().toString() + "' project.</description>",
											"<javasdk>\r\n" + 
											"	<deployment_option>incremental</deployment_option>\r\n" + 
											"</javasdk>",
											"</vaultpackage>");
		
		//Create the necessary directories if they don't already exist
		File tmp = new File(OUTPUT_XML_FILE);
		tmp.getParentFile().mkdirs();
		
		try {
//			Files.deleteIfExists(Paths.get("", OUTPUT_XML_FILE));
			if (!Files.exists(Paths.get("", OUTPUT_XML_FILE))) {
				Path file = Files.createFile(Paths.get("", OUTPUT_XML_FILE));
				Files.write(file, lines, Charset.forName("UTF-8"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "test";
	}
	
	
	//Create the "code_package.vpk" zip file with an internal structure of "javasdk/src/main/java/com/veeva/vault/custom/<customer folders>".
	//The files to zip are retrieved from a customer provided path. If the customer path doesn't contain "com/veeva/vault/custom" folders,
	//the zip process will fail with an error.
	public static void createZipFileArray(ArrayList<String> pathToZip) throws IOException {
		
		//Check if a code_deployment_yyyy-mm-dd_x.vpk already exists. If a file does exist, increment x.
		OUTPUT_ZIP_FILE.setFileName(OUTPUT_FILE_NAME);
		OUTPUT_ZIP_FILE.setLocalDate(CURRENT_DATE);
		
		while (getPackagePath() != null) {
			OUTPUT_ZIP_FILE.setIncrement(ZIP_FILE_INCREMENT);
			ZIP_FILE_INCREMENT += 1;
		}
		
		//Create the code_deployment_yyyy-mm-dd_x.vpk file. Walk through and zip the path's directory structure and filter out any files that aren't in "com/veeva/vault/custom".
		Path outputPath = Paths.get("", OUTPUT_ZIP_FILE.getString());
		
	    try (ZipArchiveOutputStream zs = new ZipArchiveOutputStream(Files.newOutputStream(outputPath,StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
	    	zs.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
	    	ZipArchiveEntry zipXMLEntry = new ZipArchiveEntry("vaultpackage.xml");
	          
	  		for (String line : pathToZip) {
				Path path = Paths.get(line);  
				
				Stream<Path> fileWalk = Files.walk(path);
				List<Path> fileList = fileWalk.filter(pp -> (pp.toString().contains("com\\veeva\\vault\\custom\\") || 
															 pp.toString().contains("com/veeva/vault/custom/"))    && 
															 !Files.isDirectory(pp)).collect(Collectors.toList());
				
				if (fileList.size() == 0) {				
					System.out.println("There are no source files in \"" + path.toAbsolutePath().toString() + "\". "
							+ "\nSource file(s) must be within a 'com/veeva/vault/custom' directory structure.");
				}
				else {
					fileList.forEach(p -> {
		
			        	  Iterator<Path> iterate = p.iterator();
			        	  int startSequence = 0;
			        	  int endSequence = p.getNameCount();
			        	  
			        	  while (iterate.hasNext()) {
			        		  Path name = iterate.next();
		
			        		  if (name.toString().contentEquals("com")) {
			        			  
			        			  name = iterate.next();
			        			  if (name.toString().contentEquals("veeva")) {
				        			  break;
			        			  }
			        		  }
			        		  else {
			        			  startSequence += 1;
			        		  }
			        	  }		        	  

			        	  ZipArchiveEntry zipEntry;
			        	  if (System.getProperty("os.name").toLowerCase().contains("windows")){
			        	  	  zipEntry = new ZipArchiveEntry("javasdk\\src\\main\\java\\" + p.subpath(startSequence, endSequence).toString());
						  }
						  else{
							  zipEntry = new ZipArchiveEntry("javasdk/src/main/java/" + p.subpath(startSequence, endSequence).toString());
						  }
				          try {
				                zs.putArchiveEntry(zipEntry);
				                writtenBytesCount += Files.copy(p, zs);
				                zs.closeArchiveEntry();

					        	System.out.println("Adding file to package: " + p.toString());
				          } catch (IOException e) {
				                System.out.println(e);
				          }
					});
				}
		    	fileWalk.close();
	  		}
	  		
	        zs.flush();

	        if (writtenBytesCount > 0) {
				try {
					zs.putArchiveEntry(zipXMLEntry);
					Files.copy(Paths.get("", OUTPUT_XML_FILE), zs);
					System.out.println("Adding file to package: " + Paths.get("", OUTPUT_XML_FILE).toAbsolutePath().toString());
					zs.closeArchiveEntry();
				} catch (IOException e) {
					System.err.println(e);
				}
				zs.flush();
		        zs.close();
		        System.out.println("\nPackage file [" + Paths.get("", OUTPUT_ZIP_FILE.getString()).toAbsolutePath().toString()+ "] created.\n");
	        }
	        else {
		        Files.deleteIfExists(outputPath);
				System.out.println("No files were packaged. VPK was not created.\n\n");
		        zs.close();   
	        }
	        writtenBytesCount = 0;
    	}
	    catch (Exception e) {
	    	System.out.println("ERROR " + e.toString());
	    	
	        Files.deleteIfExists(outputPath);
			System.out.println("No files were packaged. VPK was not created.\n\n");  
	    }
	}	

	
	//Logic to set output VPK path according to the last modified .vpk file in the deployment/packages directory.
	public static class OutputPackageFormat {
		
		private String filePath = "";
		private String fileName = "";
		private String localDate = "";
		private int increment = 0;
		private static long lastModifiedTime = 0;
		
		public OutputPackageFormat(String filePathInput) {
			setFilePath(filePathInput);
			
			//Check for and create the necessary directories. 
			//Then load in the last modified VPK as the current VPK to import into vault.
			File tmp = new File(getFilePath());
			if (tmp.mkdirs()) {
				
	        	  if (System.getProperty("os.name").toLowerCase().contains("windows")){
	        		  System.out.println("Created the '" + getProjectPath() + "\\deployment\\packages' directory.");
				  }
				  else{
					  System.out.println("Created the '" + getProjectPath() + "/deployment/packages' directory.");
				  }
			}
			
			try {
				Stream<Path> fileWalk = Files.walk(Paths.get("", getFilePath()));
				List<Path> fileList = fileWalk.filter(pp -> !Files.isDirectory(pp)).collect(Collectors.toList());
				
				fileList.forEach(p -> {
						try {
							if (Files.getLastModifiedTime(p).toMillis() > lastModifiedTime && p.toString().endsWith(".vpk")) {
								lastModifiedTime = Files.getLastModifiedTime(p).toMillis();
								String file = p.getFileName().toString();
								setFileName(file.substring(0, file.length()-4));
								setLocalDate("");
								setIncrement(0);
							}
						} catch (IOException e) {
							System.out.println("ERROR " + e.toString()+"\n\n");
						}
				});
				fileWalk.close();
			} catch (IOException e) {
				System.out.println("ERROR " + e.toString()+"\n\n");
			}
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String fileName) {
			this.filePath = fileName;
		}
		
		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getLocalDate() {
			if (localDate != "")
				return "_" + localDate;
			else
				return localDate;
		}

		public void setLocalDate(String localDate) {
			this.localDate = localDate;
		}

		public String getIncrement() {
			if (increment != 0)
				return "_" + increment;
			else
				return "";
		}

		public void setIncrement(int increment) {
			this.increment = increment;
		}
		
		public String getString() {
		
			return getFilePath() + getFileName() + getLocalDate() + getIncrement() + ".vpk";
		}
	}

	
}

