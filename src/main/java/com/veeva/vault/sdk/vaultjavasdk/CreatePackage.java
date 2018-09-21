package com.veeva.vault.sdk.vaultjavasdk;

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

public class CreatePackage {
	
	private static final String OUTPUT_ZIP_FILE = "deploy-vpk/code/vsdk_code_package.vpk";
	private static final String OUTPUT_XML_FILE = "deploy-vpk/code/vaultpackage.xml";
	private static String PROJECT_DIRECTORY;
	private static long writtenBytesCount = 0;
	

	public static String getProjectPath() {
		Path path = Paths.get("");
		
		PROJECT_DIRECTORY = path.toAbsolutePath().getFileName().toString();
		
		System.out.println("directory: " + path.toAbsolutePath().getFileName().toString());
		System.out.println(path.toAbsolutePath().toString());
		
		return path.toAbsolutePath().toString();
	}
	
	public static String getPackagePath() {
		String packagePath = null;

		if (Files.exists(Paths.get("", OUTPUT_ZIP_FILE))){
			packagePath = Paths.get("", OUTPUT_ZIP_FILE).toAbsolutePath().toString();
		}

		return packagePath;
		
	}
	
	//Create the required "vaultpackage.xml" file for the VPK. The specifies a default deployment option of "incremental".
	public static String createXMLFile(String username) {
		
		List<String> lines = Arrays.asList( "<vaultpackage xmlns=\"https://veevavault.com/\">",
											"<name>PKG-" + PROJECT_DIRECTORY + "-code</name>",
											"<source>",
											"<vault>1</vault>",
											"<author>"+ username + "</author>",
											"</source>",
											"<summary>The " + PROJECT_DIRECTORY + " project code.</summary>",
											"<description>This VPK contains the incremental Vault Java SDK code for the " + PROJECT_DIRECTORY+ " project.</description>",
											"<javasdk>\r\n" + 
											"	<deployment_option>incremental</deployment_option>\r\n" + 
											"</javasdk>",
											"</vaultpackage>");
		
		//Create the necessary directories if they don't already exist
		File tmp = new File(OUTPUT_XML_FILE);
		tmp.getParentFile().mkdirs();
		
		try {
			Files.deleteIfExists(Paths.get("", OUTPUT_XML_FILE));
			Path file = Files.createFile(Paths.get("", OUTPUT_XML_FILE));
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "test";
	}
	
	
	
	//Create the "code_package.vpk" zip file with an internal structure of "javasdk/src/main/java/com/veeva/vault/custom/<customer folders>".
	//The files to zip are retrieved from a customer provided path. If the customer path doesn't contain "com/veeva/vault/custom" folders,
	//the zip process will fail.
	public static void createZipFile(String pathToZip) throws IOException {
		Path path = Paths.get(pathToZip);
		
		//Create the necessary directories if they don't already exist
		File tmp = new File(OUTPUT_ZIP_FILE);
		tmp.getParentFile().mkdirs();
		
		//Create the vsdk_code_package.vpk file. If the file already exists, delete it and recreate it.
		Path outputPath = Paths.get("", OUTPUT_ZIP_FILE);
		
		//Walk through and zip the path's directory structure and filter out any files that aren't in "com/veeva/vault/custom".
		Stream<Path> fileWalk = Files.walk(path);
    	if (!fileWalk.noneMatch(pp -> pp.toString().contains("com\\veeva\\vault\\custom\\") && !Files.isDirectory(pp))) {
		    try (ZipArchiveOutputStream zs = new ZipArchiveOutputStream(Files.newOutputStream(outputPath,StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
		    	zs.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
		        
			    	ZipArchiveEntry zipXMLEntry = new ZipArchiveEntry("vaultpackage.xml");
			          try {
			                zs.putArchiveEntry(zipXMLEntry);
			                Files.copy(Paths.get("", OUTPUT_XML_FILE), zs);
			                zs.closeArchiveEntry();
			          } catch (IOException e) {
			                System.err.println(e);
			          }
		    	
			        Files.walk(path)
			          .filter(pp -> pp.toString().contains("com\\veeva\\vault\\custom\\") && !Files.isDirectory(pp))
			          .forEach(p -> {
		
			        	  Iterator<Path> iterate = p.iterator();
			        	  int startSequence = 0;
			        	  int endSequence = p.getNameCount();
			        	  
			        	  while (iterate.hasNext()) {
			        		  Path name = iterate.next();
		
			        		  System.out.println(name.toString());
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
			        	  
			        	  System.out.println("p: " + p);
			        	  System.out.println("path: " + p);
			        	  System.out.println("relativize: " + path.relativize(p).toString());
			        	  DeployPlugin.outputTextField.append("Packaging file: " + p.toString() + "\n\n");
			        	  System.out.println("Packaging file: " + p.toString() + "\n\n");
			        	  System.out.println("javasdk\\src\\main\\java\\" + p.subpath(startSequence, endSequence).toString());
			        	        	  
			        	  ZipArchiveEntry zipEntry = new ZipArchiveEntry("javasdk\\src\\main\\java\\" + p.subpath(startSequence, endSequence).toString());
				          try {
				                zs.putArchiveEntry(zipEntry);
				                Files.copy(p, zs);
				                zs.closeArchiveEntry();
				          } catch (IOException e) {
				                System.err.println(e);
				          }
			         });
		    	
		    	fileWalk.close();
		        zs.flush();
		        zs.close();
		         DeployPlugin.outputTextField.append("Successfully created VPK: " + Paths.get("", OUTPUT_ZIP_FILE).toAbsolutePath().toString()+ "\n\n");
	    	}
		    catch (Exception e) {
		    	 DeployPlugin.outputTextField.append("ERROR " + e.toString());
		    }
	    }
	    else {
	    	
    	 DeployPlugin.outputTextField.append("Source directory format is invalid - it must contain a 'com/veeva/vault/custom' directory structure. VPK was not created.\n\n");
	
	    }
	}
	
	
	
	
	
	//Create the "code_package.vpk" zip file with an internal structure of "javasdk/src/main/java/com/veeva/vault/custom/<customer folders>".
	//The files to zip are retrieved from a customer provided path. If the customer path doesn't contain "com/veeva/vault/custom" folders,
	//the zip process will fail.
	public static void createZipFileArray(ArrayList<String> pathToZip) throws IOException {
		
		
		//Create the necessary directories if they don't already exist
		File tmp = new File(OUTPUT_ZIP_FILE);
		tmp.getParentFile().mkdirs();
		
		//Create the vsdk_code_package.vpk file. If the file already exists, delete it and recreate it.
		Path outputPath = Paths.get("", OUTPUT_ZIP_FILE);
		
		//Walk through and zip the path's directory structure and filter out any files that aren't in "com/veeva/vault/custom".
	    try (ZipArchiveOutputStream zs = new ZipArchiveOutputStream(Files.newOutputStream(outputPath,StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
	    	zs.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
	    	ZipArchiveEntry zipXMLEntry = new ZipArchiveEntry("vaultpackage.xml");
	          try {
	                zs.putArchiveEntry(zipXMLEntry);
	                Files.copy(Paths.get("", OUTPUT_XML_FILE), zs);
	                zs.closeArchiveEntry();
	          } catch (IOException e) {
	                System.err.println(e);
	          }
	          
	          
	  		for (String line : pathToZip) {
				Path path = Paths.get(line);  
				Stream<Path> fileWalk = Files.walk(path);
				List<Path> fileList = fileWalk.filter(pp -> pp.toString().contains("com\\veeva\\vault\\custom\\") && !Files.isDirectory(pp)).collect(Collectors.toList());
				
				if (fileList.size() == 0) {
					DeployPlugin.outputTextField.append("Source directory format is invalid for \"" + path.toAbsolutePath().toString() + "\". "
							+ "\n\nSource file(s) must be within a 'com/veeva/vault/custom' directory structure.\n\n");
				}
				else {
					fileList.forEach(p -> {
		
			        	  Iterator<Path> iterate = p.iterator();
			        	  int startSequence = 0;
			        	  int endSequence = p.getNameCount();
			        	  long count;
			        	  
			        	  while (iterate.hasNext()) {
			        		  Path name = iterate.next();
		
			        		  System.out.println(name.toString());
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
			        	  
			        	  System.out.println("p: " + p);
			        	  System.out.println("path: " + p);
			        	  System.out.println("relativize: " + path.relativize(p).toString());
			        	  
			        	  DeployPlugin.outputTextField.append("Packaging file: " + p.toString() + "\n\n");
			        	  System.out.println("Packaging file: " + p.toString() + "\n\n");
			        	  System.out.println("javasdk\\src\\main\\java\\" + p.subpath(startSequence, endSequence).toString());
			        	        	  
			        	  ZipArchiveEntry zipEntry = new ZipArchiveEntry("javasdk\\src\\main\\java\\" + p.subpath(startSequence, endSequence).toString());
				          try {
				                zs.putArchiveEntry(zipEntry);
				                writtenBytesCount =+ Files.copy(p, zs);
				                System.out.println(zs.getBytesWritten());
				                zs.closeArchiveEntry();
				          } catch (IOException e) {
				                System.err.println(e);
				          }
					});
				}
		    	fileWalk.close();
	  		}
	  		
	        zs.flush();
	        System.out.println(zs.getBytesWritten());
	        if (writtenBytesCount > 0) {
		        zs.close();
		        DeployPlugin.outputTextField.append("Successfully created VPK: " + Paths.get("", OUTPUT_ZIP_FILE).toAbsolutePath().toString()+ "\n\n");
	        }
	        else {
		        Files.deleteIfExists(outputPath);
				DeployPlugin.outputTextField.append("No files were packaged. VPK was not created.\n\n");
		        zs.close();   
	        }
	        writtenBytesCount = 0;
    	}
	    catch (Exception e) {
	    	DeployPlugin.outputTextField.append("ERROR " + e.toString()+"\n\n");
	    }
	}	

}

