package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.util.ArrayList;
import java.util.Map;

//Object skeleton for the Bulk Operation JSON response.
public class ImportType extends ErrorType<Object>{
	
	private VaultPackage vaultPackage;
	private ArrayList<Map<String,Object>> data;
	
	public ArrayList<Map<String,Object>> getData(){
		System.out.println(vaultPackage.id);
		return data;
		
	}
	
	public class VaultPackage extends ErrorType<Object>{
		public String id;
		public String name;
		public String status;
		public ArrayList<Map<String,Object>> components;
		public String renamed;
		public String oldName;
		
	}
}
