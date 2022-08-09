package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import com.veeva.vault.vapil.api.model.response.VaultResponse;

import java.util.List;
import java.util.Map;

public class ErrorHandler {
	
	public static void logErrors(VaultResponse response) throws SecurityException, IllegalArgumentException {

		if (response != null) {
			if (response.getResponseMessage() != null) {
				System.out.println("Response Status: " + response.getResponseStatus());
				System.out.println("Response Message: " + response.getResponseMessage());

			} else {
				System.out.println("Response Status: " + response.getResponseStatus());
			}

			if (response instanceof ValidatePackageResponse) {
				String packageErrors = ((ValidatePackageResponse) response).getResponseDetails().getPackageError();
				if (packageErrors != null) {
					System.out.println(packageErrors);
				} else {
					List<String> errors = ((List<Map<String, List<String>>>) ((ValidatePackageResponse) response)
							.getResponseDetails().getPackageSteps().get(0).get("validation_errors")).get(0).get("message");
					if (packageErrors != null) {
						System.out.println("Errors: " + String.join("\n", errors));
					}
				}
			} else {
				if (response.getErrors() != null) {
					if (response.getErrors().size() > 0) {
						System.out.println("Errors: " + response.getErrors().toString());
					}
				} else {
					System.out.println("Response: " + response.getResponse());
				}
			}
		}
	}
}

