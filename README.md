# Vault Java SDK Maven Plugin

This Maven plugin provides a set of easy-to-use commands that allow you to package, validate, import, and deploy Vault Java SDK source code by using defined Maven goals.

## Prerequisites
1. [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html) Maven.

## Configuration

To make the Maven plugin available in a Vault Java SDK project, you must have a [Vapil settings file](https://github.com/veeva/vault-api-library) and create a separate JSON file with the following attributes:

```
{
  "deployment_option": "replace_all", 
  "package_name": "VPK filename",
  "package_type": "migration__v",
  "package_summary": "Summary of the package",
  "package_description": "Description of the package",
  "author": "vault.user@vaultdomain.com",
  "vault_id": "000001",
  "replace_existing": true
}
```
Define the filepath of both the Vapil settings file and the plugin settings file as follows:  

```
<pluginSettingsFilePath>{absolute_file_path_of_plugin_settings_file}</pluginSettingsFilePath>
<vapilSettingsFilePath>{absolute_file_path_of_vapil_settings_file}</vapilSettingsFilePath>
```

Configure the pom.xml file as follows:


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
	        	<version>24.1.0</version>
	        	<configuration>
	        		<pluginSettingsFilePath></pluginSettingsFilePath>
	        		<vapilSettingsFilePath></vapilSettingsFilePath>
	        	</configuration>
        	</plugin>
        </plugins>
    </build>    
```

## Maven Goals 

The plugin has the following Maven goals:

* **vaultjavasdk:clean** - Removes all files from the "deployment" folder of the Maven project. This folder contains VPK files and the vaultpackage.xml file created by this plugin. 

* **vaultjavasdk:package** - Generates a VPK file in the "deployment/packages" directory:
    * Names the VPK file after the `package_name` parameter in the plugin settings file.
    * Creates the directory if one does not exist.
    * If the `replace_existing` parameter is set to `true`, invokes the **clean** goal logic and creates a new package. If this parameter is set to `false` and a package of the same name exists in the "deployment/packages" directory, displays an error message requesting that you run the **clean** goal. 
    * Zips all source files under the “javasdk/src/main/java/com/veeva/vault/custom” or "src/main/java/com/veeva/vault/custom" folder in the project are zipped into a VPK file.

* **vaultjavasdk:deploy** - Validates, imports, and deploys the created package to the Vault specified in the Vapil settings file. This uses the [Validate Package](https://developer.veevavault.com/api/24.1/#validate-package) endpoint, [Import Package](https://developer.veevavault.com/api/24.1/#import-package) endpoint, and [Deploy Package](https://developer.veevavault.com/api/24.1/#deploy-package) endpoint.

* **vaultjavasdk:validate** - Validates he package that was created in the "deployment/packages" directory against the [Validate Package](https://developer.veevavault.com/api/24.1/#validate-package) endpoint.

* **vaultjavasdk:import** - Validates and imports the created package to the Vault specified in the Vapil settings file. **This is optional and is intended for verifying the package in Vault Admin UI before deploying via the Vault Admin UI**. This uses the [Validate Package](https://developer.veevavault.com/api/24.1/#validate-package) endpoint and [Import Package](https://developer.veevavault.com/api/24.1/#import-package) endpoint.


### Notes

1. The **package** goal will clean the "deployment" folder if the `replace_existing` parameter is set to `true`. If the parameter is set to `false` and a package exists in the "deployment/package/" directory, the goal will throw an error.
2. All the parameters needed to authenticate into the target Vault are defined in the Vapil settings file.
3. All parameters needed to populate the [manifest file](https://developer.veevavault.com/sdk/#create-manifest-file) are defined in the plugin settings file.
4. We recommend using the **replace_all** or **delete_all** deployment options when using this plugin. We discourage the use of the **incremental** deployment option.



## Running Goals

You can either configure the goals in your IDE or run them directly through the Maven command line. To run goals from the command line, 
navigate to your project's base directory, which is where the pom.xml file is located.

The following example will remove any files in the "deployment" folder, package the project into a VPK, and then deploys the VPK into the target Vault.
   
    > mvn vaultjavasdk:clean vaultjavasdk:package vaultjavasdk:deploy

The following example validates the named VPK in the plugin settings file:

    > mvn vaultjavasdk:validate  
    
The following example validates and then imports the named VPK in the plugin settings file:

    > mvn vaultjavasdk:import  

## License

This code serves as an example and is not meant for production use.

Copyright 2024 Veeva Systems Inc.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
