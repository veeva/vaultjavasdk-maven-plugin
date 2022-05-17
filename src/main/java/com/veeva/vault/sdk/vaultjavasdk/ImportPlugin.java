package com.veeva.vault.sdk.vaultjavasdk;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates and imports the last modified VPK in the "deployment/packages" directory to a vault. 
 * This is optional and is intended for verifying package in Vault Admin UI before deploying via the Vault Admin UI. Import Package Endpoint.
 */

@Mojo( name = "import", requiresProject = false)
public class ImportPlugin extends BaseMojo {
	@Override
	public void execute() {
		//Initializes an Authentication API connection.
		initializeVaultClient();

		try {
			if (vaultClient.validateSession()) {
				//Validates the defined VPK and then uploads it to the specified vault
				String status = null;
				
				if (!packageName.equals("")) {
					PackageManager.setPackagePath(packageName);
				}

				if (PackageManager.getPackagePath() != null) {
					PackageManager.importPackage(vaultClient, PackageManager.getPackagePath());
				}
				else {
			        System.out.println("Cannot import package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}			
			} else {
				System.out.println("Not a valid session. Check the login details in the pom file.");
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
