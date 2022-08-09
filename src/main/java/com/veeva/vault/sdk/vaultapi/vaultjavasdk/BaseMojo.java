package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

public class BaseMojo extends AbstractMojo {

    protected VaultClientId vaultClientId;
    protected VaultClient vaultClient;

    @Parameter( property = "vaultDNS", defaultValue = "", alias = "vaulturl")
    protected String vaultDNS = "";
    @Parameter( property = "username", defaultValue = "" )
    protected String username = "";
    @Parameter( property = "password", defaultValue = "" )
    protected String password = "";
    @Parameter( property = "sessionId", defaultValue = "" )
    protected String sessionId = "";
    @Parameter(property = "deploymentOption", defaultValue = "incremental")
    protected String deploymentOption = "";
    @Parameter( property = "package", defaultValue = "" )
    protected String packageName = "";
    @Parameter( property = "packageId", defaultValue = "" )
    protected String packageId = "";
    @Parameter( property = "source" )
    protected Source source = new Source();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (vaultClientId == null) {
            System.out.println("Initializing Vault Client ID");
            vaultClientId = new VaultClientId(
                    "veeva",
                    "vault",
                    "devsupport",
                    true,
                    "mavenPlugin");

            if (vaultClient == null) {
                System.out.println("Initializing Vault Client");
                VaultClientBuilder vaultClientBuilder = null;
                if (password != null && !password.isEmpty()) {
                    vaultClientBuilder = VaultClientBuilder
                            .newClientBuilder(VaultClient.AuthenticationType.BASIC)
                            .withVaultClientId(vaultClientId)
                            .withVaultUsername(username)
                            .withVaultPassword(password);
                } else if (sessionId != null && !sessionId.isEmpty()) {
                    vaultClientBuilder = VaultClientBuilder
                            .newClientBuilder(VaultClient.AuthenticationType.SESSION_ID)
                            .withVaultSessionId(sessionId);
                }
                vaultClient = vaultClientBuilder.withVaultClientId(vaultClientId)
                        .withVaultDNS(vaultDNS)
                        .build();
            }
        }
    }

}
