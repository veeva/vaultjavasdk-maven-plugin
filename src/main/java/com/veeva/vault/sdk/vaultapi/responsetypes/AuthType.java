package com.veeva.vault.sdk.vaultapi.responsetypes;
import java.util.ArrayList;

//Object skeleton for the Authentication JSON response.
@SuppressWarnings("unused")
public class AuthType extends ErrorType<Object>{
	private String sessionId;
	private String userId;
	private ArrayList<Object> vaultIds;
	private String vaultId;
}
