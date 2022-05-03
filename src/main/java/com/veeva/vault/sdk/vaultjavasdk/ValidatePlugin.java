package com.veeva.vault.sdk.vaultjavasdk;

import com.veeva.vault.sdk.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates the last modified VPK in the "deployment/packages" directory against the validation API endpoint.
 * 
 */

@Mojo( name = "validate", requiresProject = false)
public class ValidatePlugin extends BaseMojo {
	
	@Override
	public void execute() {
		//Initializes an Authentication API connection.
		initializeVaultClient();
		try {
			if (vaultClient.validateSession()) {
				//Validates the defined VPK against the specified vault.
				System.out.println("Session is valid");
				if (!packageName.equals("")) {
					PackageManager.setPackagePath(packageName);
				}
				
				if (PackageManager.getPackagePath() != null) {
					ValidatePackageResponse response = PackageManager.validatePackage(vaultClient, PackageManager.getPackagePath());

					if (response.isSuccessful()) {
						System.out.println("Validation successful");
					} else {
						ErrorHandler.logErrors(response);
					}

				}
				else {
			        System.out.println("Cannot validate package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
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
		}
	}
}
