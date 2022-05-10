package com.veeva.vault.sdk.vaultjavasdk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.sdk.vaultjavasdk.utilities.VaultAPIService;

/**
 * Goal that validates the last modified VPK in the "deployment/packages" directory against the validation API endpoint.
 * 
 */

@Mojo( name = "validate", requiresProject = false)
public class ValidatePlugin extends AbstractMojo {

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
	protected String packageFilename = "";
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
				//Validates the defined VPK against the specified vault.
				System.out.println("");
				String status = null;
				
				if (!packageFilename.equals("")) {
					PackageManager.setPackagePath(packageFilename);
				}
				
				if (PackageManager.getPackagePath() != null) {
					status = vaultClient.validatePackage(PackageManager.getPackagePath());
				}
				else {
			        System.out.println("Cannot validate package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
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
