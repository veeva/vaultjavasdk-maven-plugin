package com.veeva.vault.sdk.vaultjavasdk;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo( name = "deploy", requiresProject = false)
public class DeployPlugin extends AbstractMojo {

	protected static boolean authStatus;
	
	@Parameter( property = "apiVersion", defaultValue = "v18.3" )
	protected  String apiVersion = "";
	@Parameter( property = "vaulturl", defaultValue = "" )
	protected String vaultUrl = "";
	@Parameter( property = "username", defaultValue = "" )
	protected String username = "";
	@Parameter( property = "password", defaultValue = "" )
	protected String password = "";
	@Parameter( property = "source", defaultValue = "javasdk" )
	protected String source = "";
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		apiVersion = "/api/" + apiVersion;
		VaultAPIService vaultClient = new VaultAPIService(apiVersion, vaultUrl, username, password);
		
		
		try {
			//Initializes an Authentication API connection.
			authStatus = vaultClient.initializeAPIConnection();
			
			if (authStatus == true) {
				//Uploads the defined VPK to the specified Vault
				String importSuccess = null;
				
				if (CreatePackage.getPackagePath() != null) {
					System.out.println(CreatePackage.getPackagePath());
					importSuccess = vaultClient.importPackage(CreatePackage.getPackagePath());
				}
				else {
					UIToolPlugin.outputTextField.append("There is no vsdk_code_package.vpk in '<PROJECT_DIRECTORY>/deploy-vpk/code/'." + "\n\n");
			        System.out.println("There is no vsdk_code_package.vpk in '<PROJECT_DIRECTORY>/deploy-vpk/code/'.");
				}

//							
				if (importSuccess != null) {
					vaultClient.deployPackage(importSuccess);
				}			
			}
		} catch (MalformedURLException e) {
			getLog().info(e.toString());
		} catch (ProtocolException e) {
			getLog().info(e.toString());
		} catch (IOException e) {
			getLog().info(e.toString());
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    private String getUsername() {
    	return username;
    }

}
