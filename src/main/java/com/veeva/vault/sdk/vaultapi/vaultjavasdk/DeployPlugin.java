package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates, imports, and deploys the package that was created to the specified Vault in the Vapil Settings file.
 */

@Mojo( name = "deploy", requiresProject = false)
public class DeployPlugin extends BasePlugin {

	private static final Logger logger = Logger.getLogger(DeployPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		try {
			if (vaultClient.validateSession()) {
				//Validates, uploads, and then deploys the defined VPK to the specified vault.

				if (PACKAGE_PATH != null) {
					VaultPackage.deployPackage(vaultClient, PACKAGE_PATH);
				}
				else {
					logger.error("Cannot deploy package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}
			}
		} catch (InterruptedException e) {
			logger.error("An error has occurred. " + e.getMessage());
		}
	}
}
