package com.veeva.vault.sdk.vaultapi.responsetypes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

//Object skeleton for the VQL Query JSON response.
@SuppressWarnings("unused")
public class QueryType extends ErrorType<Object> {
	private Map<String,String> responseDetails;
	private ArrayList<Map<String,Object>> data;
	
	public ArrayList<Map<String,Object>> getData(){
		return data;
		
	}	
}
