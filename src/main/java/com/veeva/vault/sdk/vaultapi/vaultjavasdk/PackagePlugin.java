package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.VaultPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Goal that generates a VPK file in the "deployment/packages" directory.
 * 
 * 		1) VPK file is named after the "package_name" parameter in the plugin settings file
 * 		2) If the directory does not exist, it will be created.
 * 		3) If the "replace_existing" parameter is set to true, the clean goal logic is invoked and a new package is created.
 * 		If this parameter is set to false and a package of the same name exists in the "deployment/packages" directory,
 * 		then an error message will be displayed requesting that you clean.
 * 		4) Source files under the “javasdk/src/main/java/com/veeva/vault/custom” or "src/main/java/com/veeva/vault/custom" folder in the project are zipped into a VPK file.
 * 
 */

@Mojo( name = "package", requiresProject = false)
public class PackagePlugin extends BasePlugin {

	private static final Logger logger = LogManager.getLogger(PackagePlugin.class);


	@Override
	public void execute() {
		try {
			super.execute();
			if (!errorLogged) {
				if (vaultClient != null && pluginSettings != null) {
					StringBuilder sourcePath = new StringBuilder(USER_DIR);

					if (Files.exists(Paths.get("", "javasdk/src/main/java/"))) {
						sourcePath.append("/javasdk/");
					} else if (Files.exists(Paths.get("", "src/main/java/"))) {
						sourcePath.append("/src/");
					}

					boolean contentsDeleted = true;
					if (pluginSettings.getReplaceExisting()) {
						if (Files.exists(Paths.get(DEPLOYMENT_DIRECTORY))) {
							if (PackageManager.cleanPackageDirectory()) {
								logger.info("Deployment folder contents deleted.");
							} else {
								contentsDeleted = false;
								logger.error("Deployment folder doesn't exist or contents could not be deleted.");
								errorLogged = true;
							}
						}
					}

					if (contentsDeleted) {
						VaultPackage vaultPackage = new VaultPackage();
						vaultPackage.createManifest(pluginSettings, new File(DEPLOYMENT_DIRECTORY));
						vaultPackage.packAll(new File(sourcePath.toString()), new File(VPK_OUTPUT_DESTINATION), new File(DEPLOYMENT_DIRECTORY));
					}
				}
			}
		} catch (IllegalArgumentException | MojoExecutionException | MojoFailureException e) {
			if (!errorLogged) {
				logger.error("An error has occurred: " + e.getMessage());
				errorLogged = true;
			}
		}
	}

}
