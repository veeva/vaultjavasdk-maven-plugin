package com.veeva.vault.sdk.vaultjavasdk.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;

import com.veeva.vault.sdk.vaultapi.responsetypes.DeployResultsType;
import com.veeva.vault.sdk.vaultapi.responsetypes.ErrorType;
import com.veeva.vault.sdk.vaultapi.responsetypes.GenericType;
import com.veeva.vault.sdk.vaultjavasdk.UIToolPlugin;

import java.nio.file.StandardOpenOption;

public class ErrorHandler {
	
	public static void logErrors(ErrorType<?> response) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		 System.out.println(response.getField("responseStatus").toString().toUpperCase() + " Error: " + (String) response.getField("responseMessage")+ "\n\n");
		 System.out.println("Package Deployment Error: " + (String) response.getField("responseMessage"));
		 System.out.println("Error Type:" + (String) response.getErrors().toString());	
		
	}
}

