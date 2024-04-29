package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;


/**
 * Goal that deletes all files in the “deployment” folder in the maven project.
 * 
 * This will delete the packages and vaultpackage.xml.
 * 
 */

@Mojo( name = "clean", requiresProject = false)
public class CleanPlugin extends BasePlugin {
	private static final Logger logger = LogManager.getLogger(CleanPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			super.execute();
			if (!errorLogged) {
				if (PackageManager.cleanPackageDirectory()) {
					logger.info("Deployment folder contents deleted.");
				} else {
					logger.error("Deployment folder contents could not be deleted.");
					errorLogged = true;
				}
			}
		} catch (IllegalArgumentException e) {
			if (!errorLogged) {
				logger.error("An error occured when cleaning: " + e.getMessage());
				this.errorLogged = true;
			}
		}

	}
	
}
