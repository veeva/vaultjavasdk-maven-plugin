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

/**
 * Goal that validates, imports, and deploys the last modified VPK in the "deployment/packages" directory it to a vault. 
 */

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
	@Parameter( property = "sessionId", defaultValue = "" )
	protected String sessionId = "";
	@Parameter( property = "source" )
	protected Source source = new Source();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		apiVersion = "/api/" + apiVersion;
		VaultAPIService vaultClient = new VaultAPIService(apiVersion, vaultUrl, username, password, sessionId);
		
		
		try {
			//Initializes an Authentication API connection.
			authStatus = vaultClient.verifySession();
			
			if (authStatus == true) {
				//Validates, uploads, and then deploys the defined VPK to the specified vault.
				String status = null;
				System.out.println("");
				
				if (PackageManager.getPackagePath() != null) {
					status = vaultClient.validatePackage(PackageManager.getPackagePath());
					System.out.println("");
					
					if (status != null) {
						status = vaultClient.importPackage(PackageManager.getPackagePath());
					}
				}
				else {
			        System.out.println("Cannot deploy package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}
		
				if (status != null) {
					System.out.println("");
					String job_id = vaultClient.deployPackage(status);
					if (job_id != null) {
						System.out.println("Deployment in progress...");
						String jobStatus = "RUNNING";
						while (jobStatus.contentEquals("RUNNING")) {
							TimeUnit.SECONDS.sleep(12);
							jobStatus = vaultClient.jobStatus(job_id);
						}
						
						if (!jobStatus.contentEquals("RUNNING") && !jobStatus.contentEquals("FAILURE") && !jobStatus.contentEquals("EXCEPTION")) {
							System.out.println("");
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
