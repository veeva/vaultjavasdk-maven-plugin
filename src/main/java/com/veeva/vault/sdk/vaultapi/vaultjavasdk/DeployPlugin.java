package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates, imports, and deploys the last modified VPK in the "deployment/packages" directory it to a vault. 
 */

@Mojo( name = "deploy", requiresProject = false)
public class DeployPlugin extends BaseMojo {

	private static final Logger logger = Logger.getLogger(DeployPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		try {
			if (vaultClient.validateSession()) {
				//Validates, uploads, and then deploys the defined VPK to the specified vault.

				if (!packageName.equals("")) {
					PackageManager.setPackagePath(packageName);
				}

				if (PackageManager.getPackagePath() != null) {

					PackageManager.deployPackage(vaultClient, PackageManager.getPackagePath());
				}
				else {
					logger.error("Cannot deploy package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
