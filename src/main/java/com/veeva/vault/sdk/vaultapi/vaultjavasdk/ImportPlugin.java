package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import com.veeva.vault.vapil.api.model.response.PackageImportResultsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Goal that validates and imports the package that was created to the specified Vault in the Vapil Settings file.
 * This is optional and is intended for verifying package in Vault Admin UI before deploying via the Vault Admin UI.
 */

@Mojo( name = "import", requiresProject = false)
public class ImportPlugin extends BasePlugin {

	private static final Logger logger = LogManager.getLogger(ImportPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			super.execute();
			if (!errorLogged) {
				//Initializes an Authentication API connection.
				if (vaultClient != null) {
					//Validates the defined VPK and then uploads it to the specified vault

					if (PACKAGE_PATH != null) {
						PackageImportResultsResponse importResponse = VaultPackage.importPackage(vaultClient, PACKAGE_PATH);

						if (importResponse.isSuccessful()) {
							String packageStatus = importResponse.getVaultPackage().getPackageStatus();
							if (packageStatus.equals("blocked__v")) {
								logger.info("The VPK has imported successfully but has a BLOCKED status. The logs have been downloaded to the deployment/logs/ directory.");
								String validationLogUrl = importResponse.getVaultPackage().getLog().get(0).getUrl();

								ErrorHandler.retrieveImportLogs(vaultClient, validationLogUrl);
							}
						}
					} else {
						logger.error("Cannot import package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
						errorLogged = true;
					}
				} else {
					logger.error("Not a valid session. Check the login details in the pom file.");
					errorLogged = true;
				}
			}
		} catch (IllegalArgumentException | IOException | InterruptedException e) {
			if (!errorLogged) {
				logger.error("An error has occurred: " + e.getMessage());
				errorLogged = true;
			}
		}
	}
}
