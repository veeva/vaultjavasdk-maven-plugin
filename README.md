First, install the maven plugin locally - this can be done by downloading the project and then running the install Maven goal. Or, install the target/vaultjavasdk-maven-plugin-1.0.0.jar directly:

> mvn install:install-file -Dfile="<directory>\vaultjavasdk-maven-plugin-1.0.0.jar" -DpomFile="<directory\vaultjavasdk-maven-plugin-1.0.0.pom"

Next, to make this Maven plugin available in a Vault Java SDK project, add the following to the project's pom.xml file:

    <build>    
        <plugins>
            <plugin>
        		<groupId>com.veeva.vault.sdk</groupId>
	        	<artifactId>vaultjavasdk-maven-plugin</artifactId>
	        	<version>1.0.0</version>
	        	<configuration>
	        		<vaultUrl>https://<vault-url>.veevavault.com</vaultUrl>
	        		<username>user@example.com</username>
	        		<password></password>
	        		<source></source>
	        		<apiVersion>v18.3</apiVersion>
	        	</configuration>
        	</plugin>
        </plugins>
    </build>    
    
    
Then, you just setup a Maven build goal of "vaultjavasdk:deploy" and run the plugin on that project.