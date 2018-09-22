package com.veeva.vault.sdk.vaultjavasdk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo( name = "package", requiresProject = false)
public class PackagePlugin extends AbstractMojo {

	@Parameter( property = "apiVersion", defaultValue = "v18.3" )
	protected  String apiVersion = "";
	@Parameter( property = "vaulturl", defaultValue = "" )
	protected String vaultUrl = "";
	@Parameter( property = "username", defaultValue = "" )
	protected String username = "";
	@Parameter( property = "password", defaultValue = "" )
	protected String password = "";
	@Parameter( property = "source", defaultValue = "javasdk" )
	protected String source = "";
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		String filePath = CreatePackage.getSourcePath(source);
	      
	    ArrayList<String> filePathArray = new ArrayList<String>();
	      
	    filePathArray.add(filePath);

	      
	    try {
	    	CreatePackage.createXMLFile(getUsername());  
			CreatePackage.createZipFileArray(filePathArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    private String getUsername() {
    	return username;
    }

}
