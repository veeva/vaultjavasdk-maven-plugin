package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates and imports the package that was created to the specified Vault in the Vapil Settings file.
 * This is optional and is intended for verifying package in Vault Admin UI before deploying via the Vault Admin UI.
 */

@Mojo( name = "import", requiresProject = false)
public class ImportPlugin extends BasePlugin {

	private static final Logger logger = Logger.getLogger(ImportPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		//Initializes an Authentication API connection.

		try {
			if (vaultClient.validateSession()) {
				//Validates the defined VPK and then uploads it to the specified vault

				if (PACKAGE_PATH != null) {
					VaultPackage.importPackage(vaultClient, PACKAGE_PATH);
				}
				else {
			        logger.error("Cannot import package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}			
			} else {
				logger.error("Not a valid session. Check the login details in the pom file.");
			}
		} catch (SecurityException | IllegalArgumentException | InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("An error has occurred. " + e.getMessage());
		}
	}
}
