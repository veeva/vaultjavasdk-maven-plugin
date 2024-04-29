package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import com.veeva.vault.vapil.api.model.VaultModel;
import com.veeva.vault.vapil.api.model.common.PackageStep;
import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Files;

/**
 * Goal that validates he package that was created in the "deployment/packages" directory against the validation API endpoint.
 */

@Mojo( name = "validate", requiresProject = false)
public class ValidatePlugin extends BasePlugin {

	private static final Logger logger = LogManager.getLogger(ValidatePlugin.class);


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			super.execute();
			if (!errorLogged) {
				//Initializes an Authentication API connection.
				if (vaultClient != null) {
					//Validates the defined VPK against the specified vault.
					logger.info("Session is valid");

					if (PACKAGE_PATH != null && Files.exists(PACKAGE_PATH)) {
						ValidatePackageResponse response = VaultPackage.validatePackage(vaultClient, PACKAGE_PATH);

						if (response.isSuccessful()) {
							PackageStep firstPackageStep = response.getResponseDetails().getPackageSteps().get(0);
							if (firstPackageStep.getValidationResponse().equals("SUCCESS")) {
								logger.info(response.getResponseDetails().getPackageSteps().get(0).getValidationMessage());
							} else {
								VaultModel validationErrors = (VaultModel) firstPackageStep.get("validation_errors");
								logger.error(validationErrors.get("message"));
							}
						} else {
							ErrorHandler.logErrors(response);
						}

					} else {
						logger.error("Cannot validate package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
						errorLogged = true;
					}
				} else {
					logger.error("Not a valid session. Check the login details in the Vapil settings file.");
					errorLogged = true;
				}
			}
		} catch (SecurityException | IllegalArgumentException e) {
			if (!errorLogged) {
				logger.error("An error has occurred. " + e.getMessage());
				errorLogged = true;
			}
		}
	}
}
