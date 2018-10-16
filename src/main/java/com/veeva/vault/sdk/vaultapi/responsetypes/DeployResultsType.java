package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.util.ArrayList;
import java.util.Map;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;

//Object skeleton for the Bulk Operation JSON response.
public class DeployResultsType extends ErrorType<Object>{
	public ResponseDetails responseDetails;
	public ArrayList<Map<String,String>> package_components;
	
	public class ResponseDetails extends ErrorType<Object>{
		public int total_steps;
		public int deployed;
		public int deployed_with_warnings;
		public int deployed_with_failures;
		public int deployed_with_error;
		public int failed;
		public int skipped;
		public String package_status__v;
		public ArrayList<Map<String,String>> deployment_log;
		
	}
//	
//	public class StatusLink extends ErrorType<Object>{
//		public String rel;
//		public String href;
//		public String method;
//		public String accept;	
//	}

}
