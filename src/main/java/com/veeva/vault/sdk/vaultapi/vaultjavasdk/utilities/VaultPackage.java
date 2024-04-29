package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.*;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.PluginSettings;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data.CsvDataStep;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data.CsvManifest;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data.StepManifest;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.request.PackageDeploymentRequest;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.model.response.*;
import com.veeva.vault.vapil.api.request.ConfigurationMigrationRequest;
import com.veeva.vault.vapil.api.request.JobRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VaultPackage {

    private static final Logger logger = LogManager.getLogger(VaultPackage.class);


    public void createManifest(PluginSettings pluginSettings, File outputDirectory) {
        try {

            VpkManifest manifest = new VpkManifest();
            String name = pluginSettings.getPackageName();
            if (name == null) {
                name = "EXAMPLE";
            }
            manifest.setName(name);

            String packageType = pluginSettings.getPackageType();
            if (packageType == null) {
                packageType = "migration__v";
            }
            manifest.setPackageType(packageType);

            String summary = pluginSettings.getPackageSummary();
            if (summary == null) {
                summary = "Example";
            }
            manifest.setSummary(summary);

            String description = pluginSettings.getPackageDescription();
            if (description == null) {
                description = "Example";
            }
            manifest.setDescription(description);

            VaultSource source = new VaultSource();
            String author = pluginSettings.getAuthor();
            if (author == null) {
                author = "username@domain.com";
            }
            source.setAuthor(author);

            String vault = pluginSettings.getVaultId();
            if (vault == null) {
                vault = "00001";
            }
            source.setVault(vault);

            manifest.setVaultSource(source);

            JavaSdk javaSdk = new JavaSdk();
            JavaSdk.DeploymentOption deploymentOption = JavaSdk.DeploymentOption.NONE;
            String sdk = pluginSettings.getDeploymentOption();
            if (sdk != null) {
                deploymentOption = JavaSdk.DeploymentOption.valueOf(sdk.toUpperCase());
            }
            javaSdk.setDeploymentOption(deploymentOption);
            if (deploymentOption != JavaSdk.DeploymentOption.NONE) {
                manifest.setJavasdk(javaSdk);
            }

            createManifest(manifest, outputDirectory, VpkManifest.VAULTPACKAGE_FILENAME);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void createManifest(Object manifest, File outputDirectory, String xmlFileName) {
        try {
            File manifestFile = new File(
                    outputDirectory.getAbsolutePath()
                            + File.separator
                            + xmlFileName);

            File outputDir = manifestFile.getParentFile();
            ContentHelper.makeDirectories(outputDir);

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            xmlMapper.writeValue(manifestFile, manifest);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void packAll(File source, File outputDirectory, File manifestFile) {
        if (source.exists()) {
            String vpkFilePath = "";
            List<String> manifestFiles;
            if (source.isDirectory()) {
                manifestFiles = getManifestFiles(manifestFile);
            } else {
                manifestFiles = new ArrayList<>(
                        Arrays.asList(source.getAbsolutePath()));
            }

            for (String manifestPath : manifestFiles) {
                logger.info("Manifest Path: " + manifestPath);
                File xmlManifest = new File(manifestPath);
                String newVpkFilePath = pack(source, xmlManifest, outputDirectory);
                if (newVpkFilePath != null && !vpkFilePath.equals(newVpkFilePath)) {
                    vpkFilePath = newVpkFilePath;
                }
            }
        }
    }

    public String pack(File source, File xmlManifest, File outputFile) {
        try {
            if (xmlManifest.exists()) {
                logger.info("XML Manifest exists");

                ObjectMapper objectMapper = new XmlMapper();
                VpkManifest vpkManifest = objectMapper.readValue(
                        xmlManifest,
                        VpkManifest.class);
                cleanVpkFiles(xmlManifest.getParentFile());

                String packageName = vpkManifest.getName() + ".vpk";
                //File outputDir = outputFile.getParentFile();
                String vpkFilePath = outputFile.getAbsolutePath() + File.separator + packageName;
                File vpkFile = new File(vpkFilePath);

                if (vpkFile.exists()) {
                    throw new FileAlreadyExistsException("This package already exists. Please clean before packaging.");
                }

                List<File> files = getVpkFiles(source);
                files.add(xmlManifest);
                CompressionHelper.zipFiles(vpkFile,files,xmlManifest.getParentFile(),"javasdk",false);
                return vpkFilePath;
            } else {
                logger.info("XML Manifest does not exist");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public List<String> getVpks(File source) {
        if (source.exists()) {
            if (source.isFile()) {
                source = new File(source.getParent());
            }
        }

        try (Stream<Path> walk = Files.walk(source.toPath())) {

            return walk.map(x -> x.toString())
                    .filter(f -> f.toLowerCase().endsWith(".vpk"))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<File> getVpkFiles(File directory) {
        try (Stream<Path> walk = Files.walk(directory.toPath())) {

            Set<String> fileExtensions = new HashSet<>();
            //fileExtensions.add(".csv");
            //fileExtensions.add(".dep");
            fileExtensions.add(".java");
            //fileExtensions.add(".json");
            //fileExtensions.add(".mdl");
            //fileExtensions.add(".md5");
            fileExtensions.add(".xml");
            //fileExtensions.add(".xlsm");
            //fileExtensions.add(".xlsx");

            return ResourceHelper.getFiles(directory,fileExtensions);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void cleanVpkFiles(File directory) {
        try {
            List<File> files = getVpkFiles(directory);

            for (File componentFile : files) {
                String componentName = componentFile.getName().substring(0, componentFile.getName().lastIndexOf("."));
                String md5 = ChecksumHelper.getMd5(componentFile);

                if (componentFile.getName().toLowerCase().endsWith(".mdl")) {
                    String componentContent = new String(Files.readAllBytes(componentFile.toPath()), StandardCharsets.UTF_8);
                    boolean multiMdl = isMultiMDL(componentContent);

                    if (multiMdl) {
                        //there should not be an md5 file, so delete the file if it exists
                        String md5FilePath = componentFile.getParent() + File.separator + componentName + ".md5";
                        File md5File = ResourceHelper.getFile(md5FilePath);
                        if (md5File != null && md5File.exists()) {
                            md5File.delete();
                        }

                        boolean hasChange = false;
                        StepManifest stepManifest;
                        String xmlFilePath = componentFile.getAbsolutePath().substring(0, componentFile.getAbsolutePath().lastIndexOf(".")) + ".xml";
                        logger.info(xmlFilePath);
                        File xmlFile = new File(xmlFilePath);
                        if (xmlFile.exists()) {
                            String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);
                            ObjectMapper objectMapper = new XmlMapper();
                            stepManifest = objectMapper.readValue(xmlContent, StepManifest.class);
                        }
                        else {
                            stepManifest = new StepManifest();
                            stepManifest.setLabel(componentName);
                        }

                        if (!md5.equals(stepManifest.getChecksum())) {
                            stepManifest.setChecksum(md5);
                            hasChange = true;
                        }

                        if (hasChange) {
                            logger.info("RECREATE: " + xmlFile);
                            XmlMapper xmlMapper = new XmlMapper();
                            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                            xmlMapper.writeValue(xmlFile, stepManifest);
                        }
                        else {
                            logger.info("VALID: " + xmlFile);
                        }
                    }
                    else {
                        String md5FilePath = componentFile.getParent() + File.separator + componentName + ".md5";
                        File md5File = new File(md5FilePath);
                        String validMd5Content = md5 + " " + componentName;

                        if (md5File.exists()) {
                            String existingMd5Content = new String(Files.readAllBytes(md5File.toPath()), StandardCharsets.UTF_8);
                            if ((existingMd5Content == null) || (!existingMd5Content.equals(validMd5Content))) {
                                logger.info("RECREATE: " + md5FilePath);
                                ContentHelper.writeFileContent(Paths.get(md5FilePath).toFile(), validMd5Content);
                            } else {
                                logger.info("VALID: " + md5FilePath);
                            }
                        } else {
                            logger.info("CREATE: " + md5FilePath);
                            ContentHelper.writeFileContent(Paths.get(md5FilePath).toFile(), validMd5Content);
                        }
                    }
                }
                else if (componentFile.getName().toLowerCase().endsWith(".csv")) {
                    boolean hasChange = false;
                    CsvManifest csvManifest;
                    String xmlFilePath = componentFile.getAbsolutePath().substring(0, componentFile.getAbsolutePath().lastIndexOf(".")) + ".xml";
                    logger.info(xmlFilePath);
                    File xmlFile = new File(xmlFilePath);
                    if (xmlFile.exists()) {
                        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);

                        ObjectMapper objectMapper = new XmlMapper();
                        csvManifest = objectMapper.readValue(xmlContent, CsvManifest.class);

                    }
                    else {
                        csvManifest = new CsvManifest();
                        csvManifest.setLabel(componentFile.getName());
                        CsvDataStep csvDataStep = new CsvDataStep();
                        csvManifest.setCsvDataStep(csvDataStep);
                    }


                    if (!md5.equals(csvManifest.getChecksum())) {
                        csvManifest.setChecksum(md5);
                        hasChange = true;
                    }

                    CsvDataStep csvDataStep = csvManifest.getCsvDataStep();
                    if (csvDataStep != null) {
                        int rowCount = ContentHelper.getCsvRowCount(componentFile) - 1;
                        if ((csvDataStep.getRecordCount() == null) ||
                                (csvDataStep.getRecordCount() != rowCount)) {
                            csvDataStep.setRecordCount(rowCount);
                            hasChange = true;
                        }
                    }

                    if (hasChange) {
                        logger.info("RECREATE: " + xmlFile);
                        XmlMapper xmlMapper = new XmlMapper();
                        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                        xmlMapper.writeValue(xmlFile, csvManifest);
                    }
                    else {
                        logger.info("VALID: " + xmlFile);
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public List<String> getManifestFiles(File source) {
        if (source.exists()) {
            if (source.isFile()) {
                source = new File(source.getParent());
            }
        }

        try (Stream<Path> walk = Files.walk(source.toPath())) {

            return walk.map(x -> x.toString())
                    .filter(f -> f.toLowerCase().endsWith(VpkManifest.VAULTPACKAGE_FILENAME))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public boolean isMultiMDL(String mdl) {
        if (mdl != null) {
            //add a beginning semi-colon and replace all spaces, tabs and returns with nothing
            String testMdl = ";" + StringUtils.normalizeSpace(mdl).replace(" ", "");
            int commandCount = 0;
            for (MdlResponse.CommandType commandType : MdlResponse.CommandType.values()) {
                commandCount += StringUtils.countMatches(testMdl, ";" + commandType.getValue());
            }
            //value should be 1 if it's a single mdl, and greater than 1 if it's multi
            return (commandCount > 1);
        }
        return false;
    }

    public static ValidatePackageResponse validatePackage(VaultClient vaultClient, Path packagePath) {
        ValidatePackageResponse response = vaultClient.newRequest(ConfigurationMigrationRequest.class)
                .setInputPath(packagePath.toString())
                .validatePackage();

        return response;
    }

    public static PackageImportResultsResponse importPackage(VaultClient vaultClient, Path packagePath) throws InterruptedException {

        PackageImportResultsResponse vaultPackageResponse = null;

        ValidatePackageResponse validationPackageResponse = validatePackage(vaultClient, packagePath);

        String validationResponse = validationPackageResponse.getResponseDetails().getPackageSteps().get(0).getValidationResponse();

        if (validationPackageResponse != null && validationPackageResponse.isSuccessful()) {
            JobCreateResponse response;
            logger.info("Validation Successful");
            logger.info("Importing package");
            response = vaultClient.newRequest(ConfigurationMigrationRequest.class)
                    .setInputPath(String.valueOf(packagePath))
                    .importPackage();

            if (response != null && response.isSuccessful()) {
                JobStatusResponse jobStatusResponse = getPackageJobResponse(vaultClient, response.getJobId());

                if (jobStatusResponse != null && jobStatusResponse.isSuccessful()) {
                    vaultPackageResponse = getVaultPackageImportResponse(vaultClient, jobStatusResponse);

                    logger.info("Import Status: " + vaultPackageResponse.getVaultPackage().getStatus());

                } else {
                    ErrorHandler.logErrors(jobStatusResponse);
                }
            } else {
                ErrorHandler.logErrors(response);
            }
        } else {
            ErrorHandler.logErrors(validationPackageResponse);
        }
        return vaultPackageResponse;
    }

    public static PackageDeploymentResultsResponse deployPackage(VaultClient vaultClient, Path packagePath) throws InterruptedException, IOException {

        PackageDeploymentResultsResponse packageDeploymentResponse = null;

        PackageImportResultsResponse packageImportResponse = importPackage(vaultClient, packagePath);

        if (packageImportResponse != null && packageImportResponse.isSuccessful()) {
            String packageStatus = packageImportResponse.getVaultPackage().getPackageStatus();
            if (packageStatus.equals("blocked__v")) {
                logger.info("The VPK has imported successfully but has a BLOCKED status. The logs have been downloaded");
                String validationLogUrl = packageImportResponse.getVaultPackage().getLog().get(0).getUrl();

                ErrorHandler.retrieveImportLogs(vaultClient, validationLogUrl);

            } else {
                String packageId = packageImportResponse.getVaultPackage().getId();

                JobCreateResponse deploymentJobResponse = vaultClient.newRequest(ConfigurationMigrationRequest.class)
                        .deployPackage(packageId);

                if (deploymentJobResponse != null && deploymentJobResponse.isSuccessful()) {
                    JobStatusResponse deploymentStatusResponse = getPackageJobResponse(vaultClient, deploymentJobResponse.getJobId());

                    if (deploymentStatusResponse != null) {
                        packageDeploymentResponse = getVaultPackageDeploymentResponse(vaultClient, deploymentStatusResponse);

                        if (packageDeploymentResponse != null && packageDeploymentResponse.isSuccessful()) {
                            logger.info("Package Status: " + packageDeploymentResponse.getResponseDetails().getPackageStatus());
                        } else {
                            ErrorHandler.logErrors(packageDeploymentResponse);
                        }
                    } else {
                        ErrorHandler.logErrors(deploymentJobResponse);
                    }
                } else {
                    ErrorHandler.logErrors(deploymentJobResponse);
                }
            }
        } else {
            ErrorHandler.logErrors(packageImportResponse);
        }

        return packageDeploymentResponse;
    }

    public static JobStatusResponse getPackageJobResponse(VaultClient vc, Integer jobId) throws InterruptedException {

        String status;
        JobStatusResponse jobStatusResponse;
        int ctr = 0;
        do {
            if (ctr < 10) {
                logger.info("Waiting 15 seconds to retry checking the status of the job");
                TimeUnit.SECONDS.sleep(15);
            }
            jobStatusResponse = vc.newRequest(JobRequest.class).retrieveJobStatus(jobId);
            status = jobStatusResponse.getData().getStatus();
            logger.info("Package Job Status: " + status);
            ctr++;
        }
        while (!status.equals("SUCCESS") && !status.equals("ERRORS_ENCOUNTERED") && !status.equals("CANCELLED"));
        return jobStatusResponse;
    }

    public static PackageImportResultsResponse getVaultPackageImportResponse(VaultClient vaultClient, JobStatusResponse jobStatusResponse) {

        String href = jobStatusResponse.getData().getLinks().stream().filter(link -> link.getRel().equals("artifacts")).findFirst().get().getHref();

        PackageImportResultsResponse response = vaultClient.newRequest(ConfigurationMigrationRequest.class)
                .retrievePackageImportResultsByHref(href);

        return response;
    }

    public static PackageDeploymentResultsResponse getVaultPackageDeploymentResponse(VaultClient vaultClient, JobStatusResponse jobStatusResponse) {

        String href = jobStatusResponse.getData().getLinks().stream().filter(link -> link.getRel().equals("artifacts")).findFirst().get().getHref();

        PackageDeploymentResultsResponse response = vaultClient.newRequest(PackageDeploymentRequest.class)
                .retrievePackageDeploymentResultsByHref(href);

        return response;
    }
}
