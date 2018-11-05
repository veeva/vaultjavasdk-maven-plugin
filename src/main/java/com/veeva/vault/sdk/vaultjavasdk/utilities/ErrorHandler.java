package com.veeva.vault.sdk.vaultjavasdk.utilities;

import com.veeva.vault.sdk.vaultapi.responsetypes.ErrorType;

public class ErrorHandler {
	
	public static void logErrors(ErrorType<?> response) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		if (response.getField("responseMessage") != null) {
			System.out.println("Response Status: " + response.getField("responseStatus").toString().toUpperCase());
			System.out.println("Response Message: " + (String) response.getField("responseMessage"));
			 
		}
		else {
			System.out.println("Response Status: " + response.getField("responseStatus").toString().toUpperCase());
		}
	
		if (response.getField("errorType") != null) {
			System.out.println("Error Type: " + (String) response.getField("errorType"));	
		}
		 
		if (response.getErrors().size() > 0 ) {
			System.out.println("Errors: " + response.getErrors().toString());	
		}
	}
}

