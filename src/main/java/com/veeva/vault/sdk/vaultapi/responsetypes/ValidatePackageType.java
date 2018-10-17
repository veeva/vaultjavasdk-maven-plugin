package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;

//Object skeleton for the Bulk Operation JSON response.
public class ValidatePackageType extends ErrorType<Object>{
	public ResponseDetails responseDetails = new ResponseDetails();
	public ArrayList<Map<String,String>> package_components;
	
	public class ResponseDetails extends ErrorType<Object>{
		public String summary;
		public String author;
		public String package_name;
		public String source_vault;
		public String package_status;
		public int total_steps;
		public String start_time;
		public String end_time;
		public String package_error;
		public ArrayList<PackageSteps> package_steps = new ArrayList<PackageSteps>();
		
	}
	
	public class PackageSteps extends ErrorType<Object>{
		public String name__v;
		public String step_type__v;
		public String step_label__v;
		public String step_name__v;
		public String type__v;
		public String deployment_status__v;
		public String deployment_action;
		public String validation_response;
		public String validation_message;
		public ArrayList<Map<String,ArrayList<String>>> validation_errors = new ArrayList<Map<String,ArrayList<String>>>();
	}


}
