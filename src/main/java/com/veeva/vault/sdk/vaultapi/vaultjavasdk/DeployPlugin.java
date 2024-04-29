package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import com.veeva.vault.vapil.api.model.response.PackageDeploymentResultsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Goal that validates, imports, and deploys the package that was created to the specified Vault in the Vapil Settings file.
 */

@Mojo( name = "deploy", requiresProject = false)
public class DeployPlugin extends BasePlugin {

	private static final Logger logger = LogManager.getLogger(DeployPlugin.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			super.execute();
			if (!errorLogged) {
				if (vaultClient != null) {
					//Validates, uploads, and then deploys the defined VPK to the specified vault.

					if (PACKAGE_PATH != null) {
						PackageDeploymentResultsResponse response = VaultPackage.deployPackage(vaultClient, PACKAGE_PATH);

						if (response != null && response.isSuccessful()) {
							String packageStatus = response.getResponseDetails().getPackageStatus();
							if (packageStatus.equals("blocked__v") || packageStatus.equals("deployed_with_errors__v") ||
									packageStatus.equals("error__v") || packageStatus.equals("deployed_with_failures__v")) {
								logger.info("The VPK has imported successfully but has a BLOCKED status. The logs have been downloaded to the deployment/logs/ directory.");
								String deploymentLogUrl = response.getResponseDetails().getDeploymentLog().get(1).getUrl();

								ErrorHandler.retrieveDeploymentLogs(vaultClient, deploymentLogUrl);
							}
						}
					} else {
						logger.error("Cannot deploy package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
						errorLogged = true;
					}
				}
			}
		} catch (InterruptedException | IOException e) {
			if (!errorLogged) {
				logger.error("An error has occurred. " + e.getMessage());
				errorLogged = true;
			}
		}
	}
}
