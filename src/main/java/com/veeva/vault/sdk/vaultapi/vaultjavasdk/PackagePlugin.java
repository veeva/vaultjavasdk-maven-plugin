package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
public class PackagePlugin extends BaseMojo {
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		
	    ArrayList<String> filePathArray = new ArrayList<String>();
		String parentPath = null;

		if (Files.exists(Paths.get("", "javasdk/src/main/java/"))) {
			parentPath = "javasdk/src/main/java/";
		} else if (Files.exists(Paths.get("", "src/main/java/"))) {
			parentPath = "src/main/java/";
		}
			if (parentPath != null) {
			if (source.getSource() != null) {
				
			    for (String x : source.getSource()) {
			    	if (x != null) {
					    String filePath = PackageManager.getSourcePath(x, parentPath);
					    filePathArray.add(filePath);
			    	}
			    }
			}

		    try {
		    	System.out.println("");
		    	PackageManager.createXMLFile(username, deploymentOption);
				PackageManager.createZipFileArray(filePathArray, deploymentOption);
			} catch (IOException e) {
				System.out.println("Packaging error:" + e.toString());
			}
		}
		else {
			System.out.println("Invalid Vault Java SDK source directory. The code must be in a top level of 'javasdk/src/main/java' " +
					"or 'src/main/java' structure.");
		}
	}

}
