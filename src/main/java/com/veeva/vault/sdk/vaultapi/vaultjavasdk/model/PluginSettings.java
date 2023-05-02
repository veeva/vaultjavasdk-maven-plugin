package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veeva.vault.vapil.api.model.VaultModel;

public class PluginSettings extends VaultModel {

    @JsonProperty("deployment_option")
    public String getDeploymentOption() { return getString("deploymentOption"); }
    public void setDeploymentOption(String deploymentOption) { this.set("deploymentOption", deploymentOption); }

    @JsonProperty("package_name")
    public String getPackageName() { return getString("packageName"); }
    public void setPackageName(String packageName) { this.set("packageName", packageName); }

    @JsonProperty("package_type")
    public String getPackageType() { return getString("packageType"); }
    public void setPackageType(String packageType) { this.set("packageType", packageType); }

    @JsonProperty("package_summary")
    public String getPackageSummary() { return getString("packageSummary"); }
    public void setPackageSummary(String packageSummary) { this.set("packageSummary", packageSummary); }

    @JsonProperty("package_description")
    public String getPackageDescription() { return getString("packageDescription"); }
    public void setPackageDescription(String packageDescription) { this.set("packageDescription", packageDescription); }

    @JsonProperty("author")
    public String getAuthor() { return getString("author"); }
    public void setAuthor(String author) { this.set("author", author); }

    @JsonProperty("vault_id")
    public String getVaultId() { return getString("vaultId"); }
    public void setVaultId(String vaultId) { this.set("vaultId", vaultId); }

    @JsonProperty("replace_existing")
    public Boolean getReplaceExisting() { return getBoolean("replace_existing"); }
    public void setReplaceExisting(Boolean replaceExisting) { this.set("replace_existing", replaceExisting); }

}
