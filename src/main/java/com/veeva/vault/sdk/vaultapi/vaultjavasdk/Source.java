package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;

public class Source {
	@Parameter( property = "source.packages", defaultValue = "" )
	public ArrayList<String> packages = new ArrayList<String>();
	
	@Parameter( property = "source.classes", defaultValue = "" )
	private ArrayList<String> classes  = new ArrayList<String>();
	
	public Source() {
		
	}
	
	public ArrayList<String> getClasses() {
		ArrayList<String> classesOutput = new ArrayList<String>();
		for (String x : classes) {
			if (x != null) {
				classesOutput.add(x.trim());
			}
		}
		return classesOutput;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	public ArrayList<String> getPackages() {
		ArrayList<String> packagesOutput = new ArrayList<String>();
		for (String x : packages) {
			if (x != null) {
				packagesOutput.add(x.trim());
			}
		}
		return packagesOutput;
	}
	
	public void setPackages(ArrayList<String> packages) {
		this.packages = packages;
	}
	
	public ArrayList<String> getSource() {
		
		ArrayList<String> source = new ArrayList<String>();
		if (getClasses().size() > 0) {
			source.addAll(getClasses());
		}
		
		if (getPackages().size() > 0) {
			source.addAll(getPackages());
		}
		
		if (getPackages().size() == 0 && getClasses().size() == 0) {
			source.add("");
		}
		
		return source;	
	}
	public int getLength() {
		return packages.size() + classes.size();
	}

}
