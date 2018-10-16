package com.veeva.vault.sdk.vaultjavasdk;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.sdk.vaultjavasdk.utilities.VaultAPIService;


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
	protected String[] source;
	
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
				
				if (PackageManager.getPackagePath() != null) {
					System.out.println(PackageManager.getPackagePath());
					importSuccess = vaultClient.importPackage(PackageManager.getPackagePath());
				}
				else {
					UIToolPlugin.outputTextField.append("There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'." + "\n\n");
			        System.out.println("There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}
		
				if (importSuccess != null) {
					String job_id = vaultClient.deployPackage(importSuccess);
					if (job_id != null) {
						System.out.println("Deployment in progress...");
						String jobStatus = "RUNNING";
						while (jobStatus.contentEquals("RUNNING")) {
							TimeUnit.SECONDS.sleep(10);
							jobStatus = vaultClient.jobStatus(job_id);
						}
						
						if (!jobStatus.contentEquals("RUNNING")) {
							vaultClient.deployResults(jobStatus);
						}
					}
					
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    private String getUsername() {
    	return username;
    }

}
