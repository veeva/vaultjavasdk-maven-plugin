# Vault Java SDK Maven Plugin

This Maven plugin provides an easy to use set of commands to package, validate, import, and deploy Vault Java SDK source code through the use of defined Maven goals.

## Setup

The Maven plugin must be installed locally to be used in a Vault Java SDK project. 

1. Install [Maven](https://maven.apache.org/download.cgi) on your computer. 
1. Download/clone the vaultjavasdk-maven-plugin project.
1. The plugin can either be installed by importing the Maven project into an IDE or be installing the jar file directly:
    * Import the project and select the Maven "install" goal. The JDK for the project must be set to Java 1.8+
    * Install the target/vaultjavasdk-maven-plugin-1.0.0.jar directly. The project pom.xml is used to install the plugin:
       
        > mvn install:install-file -Dfile="{{PROJECT_DIRECTORY_PATH}}\target\vaultjavasdk-maven-plugin-1.0.0.jar" -DpomFile="{{PROJECT_DIRECTORY_PATH}}\pom.xml"

## Configuration

To make the Maven plugin available in a Vault Java SDK project, add the following to the project's pom.xml file. The configuration properties can be set in either the pom.xml or as parameters when running the goals.

Configuration Parameters:    

```
<vaultUrl> - a vault’s DNS host name (e.g. vaulturl.veevavault.com with no HTTPS://)
<userName> - the vault user name to use for authentication required for using the import, deploy, and validate goals
<password> - the password for user specified above. 

      ** As a best practice, it is recommended that the password is input through the command line or via an IDE build parameter. 
      ** In general, you should avoid saving the password in the pom.xml file when possible. 
	     
<sessionId> - optional, an authenticated live user session id used instead of providing userName/password credentials
<apiVerision> - optional, defaults to v18.3
<source> - optional, specify packages or class source files to include in the VPK file; if omitted, all files in the project. This is list of parameters.
	<packages> - comma separated list of package names from the project
	<classes> - comma separated list of fully qualified java file names
```

The pom.xml configuration:


```

    <properties>
     		<vaultUrl>vaulturl.veevavault.com</vaultUrl>
    		<username>user@test.com</username>
    		<password></password>
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

## Maven Goals 

The following goals are provided by the plugin.

* **vaultjavasdk:clean** - removes all files in the “deployment” folder in the maven project. This folder contains VPK files and vaultpackage.xml file created by this plugin. 

* **vaultjavasdk:package** - generates a VPK file in the "deployment/packages" directory. 
    * VPK file name format: code_package_{mm-dd-yyyy}_{num}.vpk
    * If the directory does not exist, it will be created.
    * If a VPK already exists, increment {mm-dd-yyyy} and/or {num} 
    * Source files under the “javasdk/src/main/java/com/veeva/vault/custom” folder in the project are zipped into a VPK file.

* **vaultjavasdk:deploy** - validates, imports, and deploys the last modified VPK in the "deployment/packages" directory it to a vault. This uses the [Validation Endpoint](https://developer.veevavault.com/api/18.3/#Validate_Code), [Import Package Endpoint](https://developer.veevavault.com/api/18.3/#Import_Package_Config), and [Deploy Package Endpoint](https://developer.veevavault.com/api/18.3/#Deploy_Package_Config).

* **vaultjavasdk:validate** - validates the last modified VPK in the "deployment/packages" directory against the [Validation Endpoint](https://developer.veevavault.com/api/18.3/#Validate_Code).

* **vaultjavasdk:import** - validates and imports the last modified VPK in the "deployment/packages" directory to a vault. **This is optional and is intended for verifying package in Vault Admin UI before deploying via the Vault Admin UI**. This uses the [Validation Endpoint](https://developer.veevavault.com/api/18.3/#Validate_Code) and [Import Package Endpoint](https://developer.veevavault.com/api/18.3/#Import_Package_Config).


### Notes

1. The **validate**, **import**, and **deploy** goals will pick up the last modified ".vpk" file in the "deployment/packages" folder. This means that you can craft your own custom VPK files provided they are the last modified ".vpk" file in the "deployment/packages" folder.
2. The **package** goal won't replace the "vaultpackage.xml" file in the "deployment" folder. You can modify values in this file to meet your needs.
3. The **package** goal is run separately from the import, deploy, and validate goals. This means that any code changes will require a "vaultjavasdk:package" before running an import, deploy, or validate if you want to pick up the latest code.


## How to run

You can either configure the goals in your IDE or run them directly through the Maven command line. The following example is for running the **clean**, **package**, and then **deploy** goals through the command line when the parameters are not configured in the pom.xml:
* Navigate to your project's base directory (where the pom.xml is located) and execute the following:
   
    > mvn vaultjavasdk:clean vaultjavasdk:package vaultjavasdk:deploy -Dusername=test@user.com -Dpassword=test0000 -DvaultUrl=testurl.veevavault.com
    
    
## License

This code serves as an example and is not meant for production use.

Copyright 2018 Veeva Systems Inc.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
