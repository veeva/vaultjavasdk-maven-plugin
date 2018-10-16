package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.util.ArrayList;
import java.util.Map;

//Object skeleton for the Bulk Operation JSON response.
public class BulkType extends ErrorType<Object>{
	private ArrayList<Map<String,Object>> data;
	
	public ArrayList<Map<String,Object>> getData(){
		return data;
		
	}
}
