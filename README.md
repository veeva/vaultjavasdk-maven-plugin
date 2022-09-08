# Vault Java SDK Maven Plugin

This Maven plugin provides a set of easy-to-use commands that allow you to package, validate, import, and deploy Vault Java SDK source code by using defined Maven goals.

## Prerequisites
1. [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html) Maven.

## Configuration

To make the Maven plugin available in a Vault Java SDK project, add the following to the project's pom.xml file. The configuration properties can be set in either the pom.xml or as parameters when running the goals.

Configuration Parameters:    

```
<vaultDNS> - a vault’s DNS host name with no HTTPS://. For example, vaulturl.veevavault.com
<username> - the vault user name to use for authentication required for using the import, deploy, and validate goals
<password> - the password for user specified in <username>. As a best practice, input the password through the command line or via an IDE build parameter. Avoid saving the password in the pom.xml file 
<sessionId> - optional, an authenticated live user session id used instead of providing userName/password credentials
<package> - optional, define a custom VPK to deploy. The VPK must exist in '{{PROJECT_DIRECTORY_PATH}}/deployment/packages'
<packageId> - optional, deploy a specific imported package ID. To be used in conjunction with the vaultjavasdk:import goal. The ID can also be retrieved from the vault UI.
<source> - optional, specify packages or class source files to include in the VPK file; if omitted, all files in the project. This is list of parameters.
	<packages> - comma separated list of package names from the project
	<classes> - comma separated list of fully qualified java file names
	
* Avoid saving the password in the pom.xml file when possible. 
```

The pom.xml configuration:


```
    <pluginRepositories>
        <pluginRepository>
            <id>vaultjavasdk-maven-plugin</id>
            <url>https://veeva.github.io/vaultjavasdk-maven-plugin/maven</url>
            <releases>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </releases>
        </pluginRepository>
    </pluginRepositories>
    ...
    <build>    
        <plugins>
        	<plugin>
        		<groupId>com.veeva.vault.sdk</groupId>
	        	<artifactId>vaultjavasdk-maven-plugin</artifactId>
	        	<version>22.2.1</version>
	        	<configuration>
	        		<vaultDNS>vaulturl.veevavault.com</vaultDNS>
	        		<username>user@test.com</username>
	        		<password></password>
	        		<sessionId></sessionId>
	        		<packageId></packageId>
	        		<source>
	        			<packages></packages>
	        			<classes></classes>
	        		</source>
	        	</configuration>
        	</plugin>
        </plugins>
    </build>    
```

## Maven Goals 

The following goals:

* **vaultjavasdk:clean** - removes all files in the *deployment* folder in the maven project. This folder contains VPK files and vaultpackage.xml file created by this plugin. 

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


## Running Goals

You can either configure the goals in your IDE or run them directly through the Maven command line. To run goals from the command line, 
navigate to your project's base directory, which is where the pom.xml file is located.
   
    > mvn vaultjavasdk:clean vaultjavasdk:package vaultjavasdk:deploy -Dusername=test@user.com -Dpassword=test0000 -DvaultDNS=testurl.veevavault.com

The following example deploys a custom VPK package:

    > mvn vaultjavasdk:deploy -Dpackage=custom_package.vpk -Dusername=test@user.com -Dpassword=test0000 -DvaultDNS=testurl.veevavault.com  
    
The following example imports and then deploys a package when the parameters are not configured in the pom.xml. First, import and validate the last modified package:

    > mvn vaultjavasdk:import -Dusername=test@user.com -Dpassword=test0000 -DvaultDNS=testurl.veevavault.com  

Save the *Package ID* from the import response:

    > Import Package Request: https://testurl.veevavault.com/api/v18.3/services/package   
    Package: {{PROJECT_DIRECTORY_PATH}}\deployment\packages\code_package_xxxx-xx-xx.vpk
    Successfully imported [{{PROJECT_DIRECTORY_PATH}}\deployment\packages\code_package_xxxx-xx-xx.vpk]
    Package Name: PKG-project-name
    Package Id: 0PI000000000XXX

Now you can use the saved *Package ID* to deploy the package:

    > mvn vaultjavasdk:deploy -DpackageId=0PI000000000XXX -Dusername=test@user.com -Dpassword=test0000 -DvaultDNS=testurl.veevavault.com 
    
    
## License

This code serves as an example and is not meant for production use.

Copyright 2022 Veeva Systems Inc.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
