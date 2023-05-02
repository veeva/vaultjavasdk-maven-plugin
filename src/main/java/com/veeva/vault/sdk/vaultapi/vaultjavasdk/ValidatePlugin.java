package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Files;

/**
 * Goal that validates he package that was created in the "deployment/packages" directory against the validation API endpoint.
 */

@Mojo( name = "validate", requiresProject = false)
public class ValidatePlugin extends BasePlugin {

	private static final Logger logger = Logger.getLogger(ValidatePlugin.class);


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		//Initializes an Authentication API connection.
		try {
			if (vaultClient.validateSession()) {
				//Validates the defined VPK against the specified vault.
				logger.info("Session is valid");

				if (PACKAGE_PATH != null && Files.exists(PACKAGE_PATH)) {
					ValidatePackageResponse response = VaultPackage.validatePackage(vaultClient, PACKAGE_PATH);

					if (response.isSuccessful()) {
						logger.info("VPK is valid");
					} else {
						ErrorHandler.logErrors(response);
					}

				}
				else {
			        logger.error("Cannot validate package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}			
			} else {
				logger.error("Not a valid session. Check the login details in the pom file.");
			}
		} catch (SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error("An error has occurred. " + e.getMessage());
		}
	}
}
