package com.veeva.vault.sdk.vaultjavasdk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;

/**
 * Goal that generates a VPK file in the "deployment/packages" directory.
 * 
 * 		1) VPK file name format: code_package_{mm-dd-yyyy}_{num}.vpk
 * 		2) If the directory does not exist, it will be created.
 * 		3) If a VPK already exists, increment {mm-dd-yyyy} and/or {num}
 * 		4) Source files under the “javasdk/src/main/java/com/veeva/vault/custom” folder in the project are zipped into a VPK file.
 * 
 */

@Mojo( name = "package", requiresProject = false)
public class PackagePlugin extends AbstractMojo {

	@Parameter( property = "apiVersion", defaultValue = "v18.3" )
	protected  String apiVersion = "";
	@Parameter( property = "vaulturl", defaultValue = "" )
	protected String vaultUrl = "";
	@Parameter( property = "username", defaultValue = "" )
	protected String username = "";
	@Parameter( property = "password", defaultValue = "" )
	protected String password = "";
	@Parameter( property = "sessionId", defaultValue = "" )
	protected String sessionId = "";
	@Parameter( property = "source" )
	protected Source source = new Source();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
	    ArrayList<String> filePathArray = new ArrayList<String>();
		
		if (Files.exists(Paths.get("", "javasdk/src/main/java/"))) {
			
			if (source.getSource() != null) {
				
			    for (String x : source.getSource()) {
			    	if (x != null) {
					    String filePath = PackageManager.getSourcePath(x);
					    filePathArray.add(filePath);
			    	}
			    }
			}
 
		    try {
		    	System.out.println("");
		    	PackageManager.createXMLFile(getUsername());  
				PackageManager.createZipFileArray(filePathArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Packaging error:" + e.toString());
			}
		}
		else {
			System.out.println("Invalid Vault Java SDK source directory. The code must be in a top level 'javasdk/src/main/java' structure.");
		}
	}
	
    private String getUsername() {
    	return username;
    }

}
