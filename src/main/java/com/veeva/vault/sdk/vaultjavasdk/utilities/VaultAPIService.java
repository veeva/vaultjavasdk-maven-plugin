package com.veeva.vault.sdk.vaultjavasdk.utilities;

import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veeva.vault.sdk.vaultapi.responsetypes.*;
import com.veeva.vault.sdk.vaultjavasdk.UIToolPlugin;
import com.veeva.vault.sdk.vaultapi.responsetypes.GenericType;
import com.veeva.vault.sdk.vaultapi.responsetypes.ValidatePackageType.PackageSteps;

public class VaultAPIService {
	
	final static long sessionTimeout = TimeUnit.MINUTES.toMillis(20);
	
	private String currentSessionId = null;
	private String currentUserId = null;
	private long currentSessionTime = 0;
	private String apiVersion = null;
	private String vaultUrl = null;
	private String username = null;
	private String password = null;
	
	private static HttpsURLConnection con = null;
	private static Type GenericType = new TypeToken<GenericType>(){}.getType();
	private static Type AuthType = new TypeToken<AuthType>(){}.getType();
	private static Type ImportType = new TypeToken<ImportType>(){}.getType();
	private static Type DeployType = new TypeToken<DeployType>(){}.getType();
	private static Type JobStatusType = new TypeToken<JobStatusType>(){}.getType();
	private static Type DeployResultsType = new TypeToken<DeployResultsType>(){}.getType();
	private static Type ValidatePackageType = new TypeToken<ValidatePackageType>(){}.getType();
	
	
	public VaultAPIService(String apiVersionInput, String urlInput, String usernameInput, String passwordInput, String sessionIdInput) {
		apiVersion = apiVersionInput;
		vaultUrl = urlInput;
		username = usernameInput;
		password = passwordInput;
		currentSessionId= sessionIdInput;
		
		if (!currentSessionId.contentEquals("")) {
			currentSessionTime = System.currentTimeMillis();
		}
	}
	
//Authenticates against the provided Vault URL, username, and password.	
	public boolean initializeAPIConnection() throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		AuthType authResponse;
		
	    String urlParameters = "username="+ username +"&password=" + password;
	    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
	    	
		    try {
		        URL myurl = new URL(vaultUrl + apiVersion + "/auth");
		        con = (HttpsURLConnection) myurl.openConnection();
		
		        con.setDoOutput(true);
		        con.setRequestMethod("POST");
		        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
		            wr.write(postData);
		        }
		        
		        //Checks for a valid HTTP response code and then parses the respnse content in Java objects.
		        int responsecode = con.getResponseCode(); 
				if (responsecode != 200){
					System.out.println("Connection failure with HTTP response code: " +responsecode);
				}
				else
				{
					authResponse = (AuthType) parseAPIResponse(AuthType);
					if (authResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						authResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
						
						ErrorHandler.logErrors(authResponse);
					}
					else if (authResponse instanceof AuthType){
				        if (authResponse.getField("sessionId") != null){
				        	System.out.println("Logged into host: " + vaultUrl + " as " + username);
					        System.out.println("Session ID: " +  authResponse.getField("sessionId"));
					        
					        setCurrentSessionId((String) authResponse.getField("sessionId")); 
					        currentSessionTime = System.currentTimeMillis();
					        currentUserId = (String) authResponse.getField("userId"); ;
					        
					        return true;
				        }
				        else {
				        	System.out.println("Failure - Session ID is null: " + (String) authResponse.getField("sessionId"));
					        setCurrentSessionId(null);
					        currentUserId = null;
				        }
					}
					else {
						 UIToolPlugin.outputTextField.append("Invalid responseType object.\n\n");
						 System.out.println("Invalid responseType object.");
					}
				}
		
		    } catch (UnknownHostException e){
		    	 UIToolPlugin.outputTextField.append(e.toString() + "\n\n");
		    	System.out.println(e.toString());
		    }
		    catch (IOException e){
		    	 UIToolPlugin.outputTextField.append(e.toString() + "\n\n");
		    	System.out.println(e.toString());
		    }
		    finally {
		    	if (con != null) {
		    		con.disconnect();
		    	}
		    }
			return false;	
	}
	
	//Validates the VPK package defined by the "packagePath" variable against the defined vault.
	public String validatePackage(String packagePath) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ValidatePackageType validateResponse;
	    byte[] postData = Files.readAllBytes(Paths.get(packagePath));
	    
	    System.out.println(postData.toString());

	    try {
	        URL myurl = new URL(vaultUrl  + apiVersion + "/services/package/actions/validate");
	        con = (HttpsURLConnection) myurl.openConnection();
	        con.setDoOutput(true);
	        con.setDoInput(true);
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Authorization", getCurrentSessionId());
	        con.setRequestProperty("Content-Type", "");
	        con.setRequestProperty("Accept", "application/json");
	
	        try (DataOutputStream wr2 = new DataOutputStream(con.getOutputStream())) {
	        	System.out.println("Validate Package Request: " + myurl + "\nFile: " + packagePath);
	            wr2.write(postData);
	            wr2.flush();
	            wr2.close();
	        }	
	        
	        //Checks for a valid HTTP response code and then parses the response content in Java objects.
	        int responsecode = con.getResponseCode();
	    	if (responsecode != 200){
	    		System.out.println("Connection failure with HTTP response code: " +responsecode);
				throw new RuntimeException("HttpResponseCode: " +responsecode);	
			}
			else
			{
				validateResponse = (ValidatePackageType) parseAPIResponse(ValidatePackageType);
				
				if (validateResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						validateResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
					
					ErrorHandler.logErrors(validateResponse);
					
					if (validateResponse.responseDetails.package_steps.size() > 0) {
						for (PackageSteps packageSteps : validateResponse.responseDetails.package_steps) {
							if (packageSteps.validation_response.contentEquals("FAILURE")) {
								System.out.println(packageSteps.validation_message);
								System.out.println(packageSteps.validation_errors.toString());
							}
						}
						
					}
					
					return null;
				}
				else if (validateResponse instanceof ValidatePackageType){
			        currentSessionTime = System.currentTimeMillis();
			        
			        System.out.println("Daily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
			        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
			       
					for (PackageSteps packageSteps : validateResponse.responseDetails.package_steps) {
						if (packageSteps.validation_response.contentEquals("SUCCESS")) {
							System.out.println(packageSteps.validation_message);
					        System.out.println("Successfully validated [" + PackageManager.getPackagePath() + "]");
						}
					}
			        return (String) validateResponse.getField("responseStatus");
				}
				else {
					System.out.println("Invalid responseType object.");
					return null;
				}
			}		        
	       
	    } finally {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    }	
	    
	}
			
	
	//Imports the VPK package defined by the "packagePath" variable to the connected vault.
	public String importPackage(String packagePath) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ImportType importResponse;
	    byte[] putData = Files.readAllBytes(Paths.get(packagePath));
	    
	    System.out.println(putData.toString());

	    try {
	        URL myurl = new URL(vaultUrl  + apiVersion + "/services/package");
	        con = (HttpsURLConnection) myurl.openConnection();
	        con.setDoOutput(true);
	        con.setDoInput(true);
	        con.setRequestMethod("PUT");
	        con.setRequestProperty("Authorization", getCurrentSessionId());
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setRequestProperty("Accept", "application/json");
	
	        try (DataOutputStream wr2 = new DataOutputStream(con.getOutputStream())) {
	        	System.out.println("Import Package Request: " + myurl + "\nFile: " + packagePath);
	            wr2.write(putData);
	            wr2.flush();
	            wr2.close();
	        }	
	        
	        //Checks for a valid HTTP response code and then parses the response content in Java objects.
	        int responsecode = con.getResponseCode();
	    	if (responsecode != 200){
	    		System.out.println("Connection failure with HTTP response code: " +responsecode);
				throw new RuntimeException("HttpResponseCode: " +responsecode);	
			}
			else
			{
				importResponse = (ImportType) parseAPIResponse(ImportType);
				
				if (importResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						importResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
					
					ErrorHandler.logErrors(importResponse);
					return null;
				}
				else if (importResponse instanceof ImportType){
			        currentSessionTime = System.currentTimeMillis();
			        
			         System.out.println("Daily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
			        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
			       
			         System.out.println("Successfully imported [" + PackageManager.getPackagePath() + "]");
			         System.out.println("Package Name: " + (String) ((ImportType.VaultPackage) importResponse.getField("vaultPackage")).getField("name"));
			         System.out.println("Package Id: " + ((ImportType.VaultPackage) importResponse.getField("vaultPackage")).getField("id"));
			        
			         return (String) ((ImportType.VaultPackage) importResponse.getField("vaultPackage")).getField("id");
				}
				else {
					 System.out.println("Invalid responseType object.");
					 return null;
				}
			}		        
	       
	    } finally {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    }	
	    
	}
		
		
	//Initiates a VPK deployment against the provided package ID that exists in vault.
	public String deployPackage(String packageId) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DeployType deployResponse;
	    
	    try {
	        URL myurl = new URL(vaultUrl  + apiVersion + "/vobject/vault_package__v/" + packageId + "/actions/deploy" );
	        con = (HttpsURLConnection) myurl.openConnection();
	        con.setDoOutput(true);
	        con.setDoInput(true);
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Authorization", getCurrentSessionId());
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setRequestProperty("Accept", "application/json");
	
	        try (DataOutputStream wr2 = new DataOutputStream(con.getOutputStream())) {
	        	System.out.println("Deploy URL: " + myurl);
	            wr2.flush();
	            wr2.close();
	        }	
	        
	        //Checks for a valid HTTP response code and then parses the response content in Java objects.
	        int responsecode = con.getResponseCode();
	    	if (responsecode != 200){
	    		System.out.println("Connection failure with HTTP response code: " +responsecode);
				throw new RuntimeException("HttpResponseCode: " +responsecode);	
			}
			else
			{
				deployResponse = (DeployType) parseAPIResponse(DeployType);
				
				if (deployResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						deployResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
					
					ErrorHandler.logErrors(deployResponse);
				}
				else if (deployResponse instanceof DeployType){
			        currentSessionTime = System.currentTimeMillis();
			        
			        System.out.println("Deploy Package API." +
			        		"\nDaily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
			        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
			        System.out.println("Started Deployment Job with Id: " + deployResponse.getField("job_id"));
			        
			        return (String) deployResponse.getField("job_id");
				}
				else {
					System.out.println("Invalid responseType object.");
				}
			}		        
	       
	    } finally {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    }	
	    
		return "Complete";
	}

	
	//Checks the status of a vault job. This is used to determine if a deployment job completed successfully or not.
	public String jobStatus(String packageId) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		JobStatusType jobStatusResponse;
	    
	    try {
	        URL myurl = new URL(vaultUrl  + apiVersion + "/services/jobs/" + packageId );
	        con = (HttpsURLConnection) myurl.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("Authorization", getCurrentSessionId());
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setRequestProperty("Accept", "application/json");
	        
	        //Checks for a valid HTTP response code and then parses the respnse content in Java objects.
	        int responsecode = con.getResponseCode();
	    	if (responsecode != 200){
	    		System.out.println("Connection failure with HTTP response code: " +responsecode);
				throw new RuntimeException("HttpResponseCode: " +responsecode);	
			}
			else
			{
				jobStatusResponse = (JobStatusType) parseAPIResponse(JobStatusType);
				
				if (jobStatusResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						jobStatusResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
					
					ErrorHandler.logErrors(jobStatusResponse);
				}
				else if (jobStatusResponse instanceof JobStatusType){
			        currentSessionTime = System.currentTimeMillis();
			        
			        System.out.println("Job Status API" + 
			        		"\nDaily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
			        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
			        
			        if (((String) ((JobStatusType.StatusResponse) jobStatusResponse.getField("data")).getField("status")).contains("ERROR")) {
			        	System.out.println("Job errors: " + ((JobStatusType.StatusResponse) jobStatusResponse.getField("data")).getField("status"));
			        	return jobStatusResponse.data.links.get(1).href;
			        	
						
			        }
			        else if (jobStatusResponse.data.status.contains("RUNNING")){
			        	return jobStatusResponse.data.status;
			        }
			        else {
			        	System.out.println("Successfully deployed package: " + ((JobStatusType.StatusResponse) jobStatusResponse.getField("data")).getField("id"));
			        	return jobStatusResponse.data.links.get(1).href;
			        }
				}
				else {
					System.out.println("Invalid responseType object.");
				}
			}		        
	       
	    } finally {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    }	
	    
		return "Complete";
	}
		

	//Checks the status of a vault job. This is used to determine if a deployment job completed successfully or not.
	public String deployResults(String url) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DeployResultsType deployResultsResponse;
	    
	    try {
	        URL myurl = new URL(vaultUrl + url );
	        con = (HttpsURLConnection) myurl.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("Authorization", getCurrentSessionId());
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setRequestProperty("Accept", "application/json");
	        
	        //Checks for a valid HTTP response code and then parses the respnse content in Java objects.
	        int responsecode = con.getResponseCode();
	    	if (responsecode != 200){
	    		System.out.println("Connection failure with HTTP response code: " +responsecode);
				throw new RuntimeException("HttpResponseCode: " +responsecode);	
			}
			else
			{
				deployResultsResponse = (DeployResultsType) parseAPIResponse(DeployResultsType);
				
				if (deployResultsResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
				    deployResultsResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
					
					 ErrorHandler.logErrors(deployResultsResponse);
				}
				else if (deployResultsResponse instanceof DeployResultsType){
			        currentSessionTime = System.currentTimeMillis();
			        
			        System.out.println("Job Status API is: " + (String) deployResultsResponse.getField("responseStatus") + 
			        		"\nDaily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
			        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
			        
			        if (deployResultsResponse.responseDetails.package_status__v.contains("error")) {
			        	System.out.println(deployResultsResponse.responseDetails.deployment_log.get(0).get("url"));
			        }
			        
				}
				else {
					System.out.println("Invalid responseType object.");
				}
			}		        
	       
	    } finally {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    }	
	    
		return "Complete";
	}	
	
		
	
//Using Gson, parses a HTTP JSON response into a Java Object.	
	private static GenericType<?> parseAPIResponse(Type type) throws IOException {
		StringBuilder content;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;
            content = new StringBuilder();

            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        }
        System.out.println(content.toString());
        return new Gson().fromJson(content.toString(), type);
	}
	
//Checks for a non-null and non-expired SessionId - assumes a 20 minute timeout 
//and checks the current time against the last recorded session activity.
//If there is an invalid sessionId, create a new authentication request.
	public boolean verifySession() throws MalformedURLException, ProtocolException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		
		if ((getCurrentSessionId() != null) && (System.currentTimeMillis() - currentSessionTime < sessionTimeout)) {
			
			System.out.println("Current Session Id Timeout: " + (System.currentTimeMillis() - currentSessionTime) + " < " + sessionTimeout);
	    	return true;
	    }
	    else {
	    	System.out.println("No valid Session Id; a new authentication request must be made.");
	    	return initializeAPIConnection();
	    }
	}

	public String getCurrentSessionId() {
		return currentSessionId;
	}

	public void setCurrentSessionId(String inputCurrentSessionId) {
		currentSessionId = inputCurrentSessionId;
	}
}

