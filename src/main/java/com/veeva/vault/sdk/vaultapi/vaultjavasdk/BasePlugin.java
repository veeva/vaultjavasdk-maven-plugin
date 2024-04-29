package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.PluginSettings;
import com.veeva.vault.vapil.api.client.VaultClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BasePlugin extends AbstractMojo {

    protected VaultClient vaultClient;

    @Parameter( property = "vapilSettingsFilePath", defaultValue = "")
    protected String vapilSettingsFilePath= "";
    @Parameter( property = "pluginSettingsFilePath", defaultValue = "")
    protected String pluginSettingsFilePath;
    protected PluginSettings pluginSettings;
    protected String PACKAGE_NAME = null;
    protected Path PACKAGE_PATH = null;
    protected String IMPORT_LOG_FILE = LOG_OUTPUT_DESTINATION + "import-log";
    protected String DEPLOYMENT_LOG_FILE = LOG_OUTPUT_DESTINATION + "deployment-log";
    protected static final String USER_DIR = System.getProperty("user.dir");
    protected static final String DEPLOYMENT_DIRECTORY = USER_DIR + "/deployment/";
    protected static final String VPK_OUTPUT_DESTINATION = USER_DIR + "/deployment/packages/";
    protected static final String LOG_OUTPUT_DESTINATION = USER_DIR + "/deployment/logs/";
    protected boolean errorLogged = false;

    private static final Logger logger = LogManager.getLogger(BasePlugin.class);


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        boolean errorLogged = false;


        if (vaultClient == null) {
            try {
                if (vapilSettingsFilePath != null) {

                    vaultClient = VaultClient.newClientBuilderFromSettings(convertFileToString(vapilSettingsFilePath))
                            .build();

                } else {
                    logger.error("File path for Vapil settings is not present.");
                    this.errorLogged = true;
                }

                if (vaultClient.validateSession() && pluginSettingsFilePath != null) {
                    this.pluginSettings = mapSettingValues(pluginSettingsFilePath);


                    if (pluginSettings != null) {
                        this.PACKAGE_NAME = pluginSettings.getPackageName() + ".vpk";
                        this.PACKAGE_PATH = Paths.get(VPK_OUTPUT_DESTINATION, PACKAGE_NAME);
                    }
                }
            } catch (IllegalArgumentException | IOException e) {
                StackTraceElement[] stackTrace = e.getStackTrace();

                String className = null;
                if (stackTrace.length > 0) {
                    StackTraceElement topStackFrame = stackTrace[0];
                    className = topStackFrame.getClassName();
                }
                if (className != null && !className.contains("vapil") && !errorLogged) {
                    logger.error("An error occurred: " + e.getMessage());
                    this.errorLogged = true;
                }
            }
        }
    }

    private static PluginSettings mapSettingValues(String pluginSettingsFilePath) throws IOException, IllegalArgumentException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(convertFileToString(pluginSettingsFilePath), PluginSettings.class);

    }

    private static String convertFileToString(String filePath) throws IOException {
        File file = new File(filePath);
        return new String(Files.readAllBytes(Paths.get(file.getPath())));
    }
}
