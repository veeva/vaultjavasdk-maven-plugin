package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.util.ArrayList;
import java.util.Map;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;

//Object skeleton for the Bulk Operation JSON response.
public class DeployResultsType extends ErrorType<Object>{
	public ArrayList<Map<Object,Object>> responseDetails;
	public ArrayList<Map<Object,Object>> package_components;
	
	public class StatusResponse extends ErrorType<Object>{
		public String id;
		public String status;
		public String method;
		public ArrayList<StatusLink> links;
		public int created_by;
		public String created_date;
		public String run_start_date;
		public String run_end_date;
		
	}
	
	public class StatusLink extends ErrorType<Object>{
		public String rel;
		public String href;
		public String method;
		public String accept;	
	}

}
