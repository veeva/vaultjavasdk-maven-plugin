package com.veeva.vault.sdk.vaultapi.responsetypes;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

//Object skeleton for the Error JSON responses.
@SuppressWarnings("unused")
public class ErrorType<T> extends GenericType<Object>{
	private ArrayList<Map<String,String>> errors;
	private String errorType;
	
	public ArrayList<Map<String,String>> getErrors(){
		return errors;
	}
}
