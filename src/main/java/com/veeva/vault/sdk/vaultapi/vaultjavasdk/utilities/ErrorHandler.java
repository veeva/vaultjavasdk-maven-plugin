package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import com.veeva.vault.vapil.api.model.response.VaultResponse;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ErrorHandler {

	private static final Logger logger = Logger.getLogger(ErrorHandler.class);

	public static void logErrors(VaultResponse response) throws SecurityException, IllegalArgumentException {

		if (response != null) {
			if (response.getResponseMessage() != null) {
				logger.error("Response Status: " + response.getResponseStatus());
				logger.error("Response Message: " + response.getResponseMessage());
			} else {
				logger.error("Response Status: " + response.getResponseStatus());
			}

			if (response instanceof ValidatePackageResponse) {
				String packageErrors = ((ValidatePackageResponse) response).getResponseDetails().getPackageError();
				if (packageErrors != null) {
					logger.error(packageErrors);
				} else {
					List<String> errors = ((List<Map<String, List<String>>>) ((ValidatePackageResponse) response)
							.getResponseDetails().getPackageSteps().get(0).get("validation_errors")).get(0).get("message");
					if (packageErrors != null) {
						logger.error("Errors: " + String.join("\n", errors));
					}
				}
			} else {
				if (response.getErrors() != null) {
					if (response.getErrors().size() > 0) {
						logger.error("Errors: " + response.getErrors().toString());
					}
				} else {
					logger.error("Response: " + response.getResponse());
				}
			}
		}
	}
}

