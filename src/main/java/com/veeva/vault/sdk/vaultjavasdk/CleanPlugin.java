package com.veeva.vault.sdk.vaultjavasdk;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;

/**
 * Goal that deletes all files in the “deployment” folder in the maven project. .
 * 
 * This will delete the packages, log files, and vaultpackage.xml.
 * 
 */

@Mojo( name = "clean", requiresProject = false)
public class CleanPlugin extends AbstractMojo {

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
	protected Source source;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if (PackageManager.cleanPackageDirectory()) {
			System.out.println("Deployment folder contents deleted.");
		}
		else {
			System.out.println("Deployment folder contents could not be deleted.");
		}

	}
	
}
