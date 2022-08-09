package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that deletes all files in the “deployment” folder in the maven project. .
 * 
 * This will delete the packages, log files, and vaultpackage.xml.
 * 
 */

@Mojo( name = "clean", requiresProject = false)
public class CleanPlugin extends BaseMojo {
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		if (PackageManager.cleanPackageDirectory()) {
			System.out.println("Deployment folder contents deleted.");
		}
		else {
			System.out.println("Deployment folder contents could not be deleted.");
		}

	}
	
}
