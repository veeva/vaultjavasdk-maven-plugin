To make this Maven plugin available in a project, add the following to the project's pom.xml file:

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
	        		<apiVersion>v18.3</apiVersion>
	        	</configuration>
        	</plugin>
        </plugins>
    </build>    