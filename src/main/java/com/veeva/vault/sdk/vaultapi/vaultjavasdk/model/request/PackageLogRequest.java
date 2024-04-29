package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.request;

import com.veeva.vault.vapil.api.model.response.VaultResponse;
import com.veeva.vault.vapil.api.request.VaultRequest;
import com.veeva.vault.vapil.connector.HttpRequestConnector;

public class PackageLogRequest extends VaultRequest {

    private String outputPath;

    public VaultResponse downloadImportLogFile(String url) {

        HttpRequestConnector request = new HttpRequestConnector(url);

        if (outputPath != null) {
            return (VaultResponse) sendToFile(HttpRequestConnector.HttpMethod.GET, request, outputPath, VaultResponse.class);
        } else {
            return (VaultResponse) sendReturnBinary(HttpRequestConnector.HttpMethod.GET, request, VaultResponse.class);
        }
    }

    public PackageLogRequest setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }
}
