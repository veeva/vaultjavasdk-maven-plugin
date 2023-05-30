package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.request.PackageLogRequest;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import com.veeva.vault.vapil.api.model.response.VaultResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ErrorHandler {

	private static final Logger logger = Logger.getLogger(ErrorHandler.class);
	private static final String LOG_OUTPUT_DESTINATION = System.getProperty("user.dir") + "/deployment/logs/";

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

	public static void retrieveImportLogs(VaultClient vaultClient, String url) throws IOException {
		retrieveLogs(vaultClient, url, LOG_OUTPUT_DESTINATION + "/vpk-import-logs");
	}

	public static void retrieveDeploymentLogs(VaultClient vaultClient, String url) throws IOException {
		retrieveLogs(vaultClient, url, LOG_OUTPUT_DESTINATION + "/vpk-deployment-logs");
	}


	private static void retrieveLogs(VaultClient vaultClient, String url, String destination) throws IOException {

		Path destinationPath = Paths.get(destination);

		if (!Files.exists(destinationPath)) {
			Files.createDirectories(destinationPath);
		}

		vaultClient.newRequest(PackageLogRequest.class)
				.setOutputPath(destination)
				.downloadImportLogFile(url);
	}
}

