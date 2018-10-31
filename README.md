# Vault Java SDK Maven Plugin

This Maven plugin assists in the packaging, validation, importation, and deployment of Vault Java SDK source code.

## Setup

The Maven plugin must be installed locally to be used in a Vault Java SDK project. 

1. Install [Maven](https://maven.apache.org/download.cgi) on your computer. 
1. Download/clone the vaultjavasdk-maven-plugin project.
1. The plugin can either be installed by importing the Maven project into an IDE or be installing the jar file directly:
    * Import the project and select the Maven "install" goal. The JDK for the project must be set to Java 1.8+
    * Install the target/vaultjavasdk-maven-plugin-1.0.0.jar directly:
        > mvn install:install-file -Dfile="<directory>\vaultjavasdk-maven-plugin-1.0.0.jar" -DpomFile="<directory\vaultjavasdk-maven-plugin-1.0.0.pom"

## Configuration

To make the Maven plugin available in a Vault Java SDK project, add the following to the project's pom.xml file. The configuration parameters can be set either the pom.xml or as parameters when running the goals:

```

	  <properties>
     		<vaultUrl>vaulturl.veevavault.com</vaultUrl>
    		<username>user@test.com</username>
    		<password>xxxxxxx</password>
    		<sessionId></sessionId>
    		<source.packages></source.packages>
    		<source.classes></source.classes>
    </properties>
    
    <build>    
        <plugins>
        	<plugin>
        		<groupId>com.veeva.vault.sdk</groupId>
	        	<artifactId>vaultjavasdk-maven-plugin</artifactId>
	        	<version>1.0.0</version>
	        	<configuration>
	        		<vaultUrl>${vaultUrl}</vaultUrl>
	        		<username>${username}</username>
	        		<password>${password}</password>
	        		<sessionId>${sessionId}</sessionId>
	        		<source>
	        			<packages>${source.packages}</packages>
	        			<classes>${source.classes}</classes>
	        		</source>
	        		<apiVersion>v18.3</apiVersion>
	        	</configuration>
        	</plugin>
        </plugins>
    </build>    
```

Configuration Parameters:    

```
<vaultUrl> - a vault’s DNS host name
<userName> - the Vault user name to use for authentication required for using the import, deploy, and validate goals
<password> - the password for user specified above. 
<sessionId> - optional, an authenticated live user session id used instead of providing userName/password credentials
<apiVerision> - optional, defaults to v18.3
<source> - optional, specify packages or class source files to include in the VPK file; if omitted, all files in the project. This is list of parameters; the parameter can be named anything.
	<packages> - comma separated list of package names from the project
	<classes> - comma separated list of fully qualified java file names
```

## Maven Goals 

The following goals are provided by the plugin.

* **vaultjavasdk:clean** - removes all files in the “deployment” folder in the maven project. This folder contains VPK files and vaultpackage.xml file created by this plugin. 

* **vaultjavasdk:package** - generates a VPK file in the "deployment/packages" directory. 
    * VPK file name format: code_package_{mm-dd-yyyy}_{num}.vpk
    * If the directory does not exist, it will be created.
    * If a VPK already exists, increment {mm-dd-yyyy} and/or {num} 
    * Source files under the “javasdk/src/main/java/com/veeva/vault/custom” folder in the project are zipped into a VPK file.

* **vaultjavasdk:validate** - validates the last modified VPK in the "deployment/packages" directory against the [validation endpoint](https://internal-developer.veevavault.com/api/18.3/#validate-package).

* **vaultjavasdk:import** - validates and imports the last modified VPK in the "deployment/packages" directory to a vault. [Import Package Endpoint](https://developer.veevavault.com/api/18.3/#import-package).

* **vaultjavasdk:deploy** - validates, imports, and deploys the last modified VPK in the "deployment/packages" directory it to a vault. [Deploy Package Endpoint](https://developer.veevavault.com/api/18.3/#deploy-package).

## How to run

You can either setup the goals in your IDE or run them directly through the Maven command line. The following example is for running the **deploy** goal through the command line when the parameters are not configured in the pom.xml:

> mvn vaultjavasdk:deploy -Dusername=test@user.com -Dpassword=test0000 -DvaultUrl=testurl.veevavault.com
    
