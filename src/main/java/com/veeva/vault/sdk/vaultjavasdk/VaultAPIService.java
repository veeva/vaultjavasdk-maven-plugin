package com.veeva.vault.sdk.vaultjavasdk;

import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veeva.vault.sdk.vaultapi.responsetypes.AuthType;
import com.veeva.vault.sdk.vaultapi.responsetypes.DeployType;
import com.veeva.vault.sdk.vaultapi.responsetypes.ImportType;
import com.veeva.vault.sdk.vaultapi.responsetypes.GenericType;

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
	private static Type AuthType = new TypeToken<AuthType>(){}.getType();
	private static Type ImportType = new TypeToken<ImportType>(){}.getType();
	private static Type DeployType = new TypeToken<DeployType>(){}.getType();
	
	
	public VaultAPIService(String apiVersionInput, String urlInput, String usernameInput, String passwordInput) {
		apiVersion = apiVersionInput;
		vaultUrl = urlInput + apiVersion;
		username = usernameInput;
		password = passwordInput;	
	}
	
//Authenticates against the provided Vault URL, username, and password.	
	public boolean initializeAPIConnection() throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		AuthType authResponse;
		
	    String urlParameters = "username="+ username +"&password=" + password;
	    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
	    	
		    try {
		        URL myurl = new URL(vaultUrl + "/auth");
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
					UIToolPlugin.outputTextField.append("Connection failure with HttpResponseCode: " +responsecode + "\n\n");
//					throw new RuntimeException("HttpResponseCode: " +responsecode);	
				}
				else
				{
					authResponse = (AuthType) parseAPIResponse(AuthType);
					if (authResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
						authResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
						
						 UIToolPlugin.outputTextField.append(authResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) authResponse.getField("responseMessage")+ "\n\n");
						 UIToolPlugin.outputTextField.append("Authentication Error: " + (String) authResponse.getField("responseMessage")+ "\n\n");
						 UIToolPlugin.outputTextField.append("Errors:" + (String) authResponse.getErrors().toString()+ "\n\n");	
						 
						 System.out.println(authResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) authResponse.getField("responseMessage")+ "\n\n");
						 System.out.println("Authentication Error: " +  authResponse.getField("responseMessage"));
						 System.out.println("Errors: " + authResponse.getErrors().toString());
					}
					else if (authResponse instanceof AuthType){
				        if (authResponse.getField("sessionId") != null){
					        System.out.println("Success - Session ID: " +  authResponse.getField("sessionId"));
					        setCurrentSessionId((String) authResponse.getField("sessionId")); 
					         UIToolPlugin.outputTextField.append("Success - Session ID: " +  authResponse.getField("sessionId") + "\n\n");
					        currentSessionTime = System.currentTimeMillis();
					        currentUserId = (String) authResponse.getField("userId"); ;
					        
					        return true;
				        }
				        else {
				        	System.out.println("Failure - Session ID is null: " + (String) authResponse.getField("sessionId"));
					        UIToolPlugin.outputTextField.append("Failure - Session ID is null: " + (String) authResponse.getField("sessionId") + "\n\n");
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
	
	
	//Initiates a bulk update to the Vault API up to the max batch size of 1000.
	//Creates a CSV formatted string and converts it into a byte array for the PUT request. 
		public String importPackage(String packagePath) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			ImportType importResponse;
		    StringBuilder urlParameters = new StringBuilder();
//		    packagePath = "C:\\Users\\nanoz\\eclipse-workspace\\vsdk-helloworld-deploy-test\\deploy-vpk\\vsdk-helloworld-code\\javasdk.zip";
		    byte[] putData = Files.readAllBytes(Paths.get(packagePath));
		    
		    System.out.println(putData.toString());

		  
			    
		    try {
		        URL myurl = new URL(vaultUrl + "/services/package");
		        con = (HttpsURLConnection) myurl.openConnection();
		        con.setDoOutput(true);
		        con.setDoInput(true);
		        con.setRequestMethod("PUT");
		        con.setRequestProperty("Authorization", getCurrentSessionId());
		        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        con.setRequestProperty("Accept", "application/json");
		
		        try (DataOutputStream wr2 = new DataOutputStream(con.getOutputStream())) {
		        	 UIToolPlugin.outputTextField.append("Upload Package Request: " + myurl + "\n"
		        									  + "File: " + packagePath + "\n\n");
		        	System.out.println("PUT to " + myurl + "\nFile: " + packagePath);
		            wr2.write(putData);
		            wr2.flush();
		            wr2.close();
		        }	
		        
		        //Checks for a valid HTTP response code and then parses the response content in Java objects.
		        int responsecode = con.getResponseCode();
		    	if (responsecode != 200){
		    		System.out.println("Error - HttpResponseCode: " + responsecode  + " " + myurl);
					throw new RuntimeException("HttpResponseCode: " +responsecode);	
				}
				else
				{
					importResponse = (ImportType) parseAPIResponse(ImportType);
					
					if (importResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
							importResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
						
						 UIToolPlugin.outputTextField.append(importResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) importResponse.getField("responseMessage") + "\n\n");
						 UIToolPlugin.outputTextField.append("Package Import Error: " + (String) importResponse.getField("responseMessage")+ "\n\n");
						 UIToolPlugin.outputTextField.append("Error Type:" + (String) importResponse.getField("errorType")+ "\n\n");	
						
						 System.out.println(importResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) importResponse.getField("responseMessage") + "\n\n");
						 System.out.println("Package Import Error: "  + (String) importResponse.getField("responseMessage"));
						 System.out.println("Error Type:" + (String) importResponse.getField("errorType"));	
						
						return null;
					}
					else if (importResponse instanceof ImportType){
				        currentSessionTime = System.currentTimeMillis();
				        
				         System.out.println("Bulk API is: " + (String) importResponse.getField("responseStatus") + 
				        		"\nDaily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
				        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
				         UIToolPlugin.outputTextField.append("Package Upload SUCCESS: \n");
				         UIToolPlugin.outputTextField.append(" * Package Name: " + (String) ((ImportType.VaultPackage) importResponse.getField("vaultPackage")).getField("name") + 
				        								  "\n * Package Id: " + ((ImportType.VaultPackage) importResponse.getField("vaultPackage")).getField("id") + "\n\n");
				        
				         System.out.println("Package Upload SUCCESS: \n");
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
		
		
	//Initiates a bulk update to the Vault API up to the max batch size of 1000.
	//Creates a CSV formatted string and converts it into a byte array for the PUT request. 
		public String deployPackage(String packageId) throws MalformedURLException, ProtocolException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			DeployType deployResponse;
		    StringBuilder urlParameters = new StringBuilder();
//			    packagePath = "C:\\Users\\nanoz\\eclipse-workspace\\vsdk-helloworld-deploy-test\\deploy-vpk\\vsdk-helloworld-code\\javasdk.zip";
		    byte[] putData = null;

		    
		    try {
		        URL myurl = new URL(vaultUrl + "/vobject/vault_package__v/" + packageId + "/actions/deploy" );
		        con = (HttpsURLConnection) myurl.openConnection();
		        con.setDoOutput(true);
		        con.setDoInput(true);
		        con.setRequestMethod("POST");
		        con.setRequestProperty("Authorization", getCurrentSessionId());
		        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        con.setRequestProperty("Accept", "application/json");
		
		        try (DataOutputStream wr2 = new DataOutputStream(con.getOutputStream())) {
		        	System.out.println("PUT to " + myurl + "\nVault Package ID: " + packageId);
		            wr2.flush();
		            wr2.close();
		        }	
		        
		        //Checks for a valid HTTP response code and then parses the respnse content in Java objects.
		        int responsecode = con.getResponseCode();
		    	if (responsecode != 200){
		    		System.out.println("Error - HttpResponseCode: " + responsecode  + " " + myurl);
					throw new RuntimeException("HttpResponseCode: " +responsecode);	
				}
				else
				{
					deployResponse = (DeployType) parseAPIResponse(DeployType);
					
					if (deployResponse.getField("responseStatus").toString().toUpperCase().contains("FAILURE")||
							deployResponse.getField("responseStatus").toString().toUpperCase().contains("EXCEPTION")){
						
						 UIToolPlugin.outputTextField.append(deployResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) deployResponse.getField("responseMessage")+ "\n\n");
						 UIToolPlugin.outputTextField.append("Package Deployment Error: " + (String) deployResponse.getField("responseMessage")+ "\n\n");
						 UIToolPlugin.outputTextField.append("Error Type:" + (String) deployResponse.getErrors().toString()+ "\n\n");	
						
						 System.out.println(deployResponse.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) deployResponse.getField("responseMessage")+ "\n\n");
						 System.out.println("Package Deployment Error: " + (String) deployResponse.getField("responseMessage"));
						 System.out.println("Error Type:" + (String) deployResponse.getErrors().toString());	
					}
					else if (deployResponse instanceof DeployType){
				        currentSessionTime = System.currentTimeMillis();
				        
				        System.out.println("Bulk API is: " + (String) deployResponse.getField("responseStatus") + 
				        		"\nDaily API Limit: "+ con.getHeaderField("X-VaultAPI-DailyLimitRemaining") +
				        		"\nBurst API Limit: "+ con.getHeaderField("X-VaultAPI-BurstLimitRemaining"));
				        
				        UIToolPlugin.outputTextField.append("Successfully started deploy Job with Id: " + deployResponse.getField("job_id") + "\n\n");
				        System.out.println("Successfully started deploy Job with Id: " + deployResponse.getField("job_id") + "\n\n");
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

