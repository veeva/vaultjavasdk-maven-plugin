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
 * Goal that validates and imports the last modified VPK in the "deployment/packages" directory to a vault. 
 * This is optional and is intended for verifying package in Vault Admin UI before deploying via the Vault Admin UI. Import Package Endpoint.
 */

@Mojo( name = "import", requiresProject = false)
public class ImportPlugin extends AbstractMojo {

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
	@Parameter( property = "package", defaultValue = "" )
	protected String packageName = "";
	@Parameter( property = "packageId", defaultValue = "" )
	protected String packageId = "";
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
				//Validates the defined VPK and then uploads it to the specified vault
				System.out.println("");
				String status = null;
				
				if (!packageName.equals("")) {
					PackageManager.setPackagePath(packageName);
				}

				if (PackageManager.getPackagePath() != null) {
					status = vaultClient.validatePackage(PackageManager.getPackagePath());
					
					if (status != null) {
						System.out.println("");
						status = vaultClient.importPackage(PackageManager.getPackagePath());
					}
				}
				else {
			        System.out.println("Cannot import package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
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
}
