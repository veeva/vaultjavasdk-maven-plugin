package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.request;

import com.veeva.vault.vapil.api.model.response.PackageDeploymentResultsResponse;
import com.veeva.vault.vapil.api.model.response.PackageImportResultsResponse;
import com.veeva.vault.vapil.api.request.JobRequest;
import com.veeva.vault.vapil.api.request.VaultRequest;

public class PackageDeploymentRequest extends VaultRequest {


    public PackageDeploymentResultsResponse retrievePackageDeploymentResultsByHref(String href) {
        return vaultClient.newRequest(JobRequest.class).retrieveJobArtifactByHref(href, PackageDeploymentResultsResponse.class);
    }
}
