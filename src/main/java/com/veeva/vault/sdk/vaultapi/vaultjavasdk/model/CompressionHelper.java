package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CompressionHelper {

    private static Logger logger = LogManager.getLogger(CompressionHelper.class);

    public static File newFile(File outputDirectory, ZipEntry zipEntry) throws IOException {
        //logger.info(zipEntry.getName());
        String newName = zipEntry.getName();
        File destFile = new File(outputDirectory, newName);

        String parentDirPath = destFile.getParent();
        File parentDir = new File(parentDirPath);
        ContentHelper.makeDirectories(parentDir);
        String destFilePath = destFile.getParent();


        if (!destFilePath.startsWith(parentDirPath)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


    public static void zipFiles(File zipFile,
                                List<File> files,
                                File relativeFile,
                                String pathInZip,
                                boolean appendToFile) throws Exception {
        if (files != null && !files.isEmpty()) {
            ContentHelper.makeDirectories(zipFile.getParentFile());
            if (!appendToFile && zipFile.exists()) {
                logger.warn("RECREATE: " + zipFile.getAbsolutePath());
                zipFile.delete();
            }
            else if (!zipFile.exists()) {
                logger.warn("CREATE: " + zipFile.getAbsolutePath());
            }
            else {
                logger.warn("APPEND: " + zipFile.getAbsolutePath());
            }

            Map<String, Object> env = new HashMap<>();
            env.put("create", "true");
            env.put("useTempFile", Boolean.TRUE.toString());
            URI uri = URI.create("jar:" + zipFile.toPath().toUri());
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

                for (File inputFile : files) {
                    logger.info("ZIP: " + inputFile.getAbsolutePath());

                    Path zipFilePath = null;
                    if (pathInZip != null) {
                        Path zipDirectory = zipfs.getPath(pathInZip);
                        if (Files.notExists(zipDirectory)) {
                            Files.createDirectories(zipDirectory);
                        }
                        zipFilePath = zipfs.getPath(pathInZip);
                        //zipFilePath.append(pathInZip + "/");
                    }

                    if (relativeFile != null) {
                        if (inputFile.getParentFile().compareTo(relativeFile) != 0) {
                            String zipEntryPath = zipFile.getParentFile().getAbsolutePath();
                            String sourceParentPath = inputFile.getParentFile().getAbsolutePath();
                            if (!relativeFile.getAbsolutePath().equals(sourceParentPath)) {
                                zipEntryPath = inputFile.getAbsolutePath().substring(relativeFile.getParent().length()).replaceAll("\\\\", "/");
                            }
                            Path zipDirectory = zipfs.getPath(zipEntryPath).getParent();
                            if (zipDirectory != null) {
//                                if (Files.notExists(zipDirectory)) {
//                                    Files.createDirectories(zipDirectory);
//                                }
                                zipFilePath = zipfs.getPath(zipFilePath.toString(), String.valueOf(zipDirectory));
                                //zipFilePath.append(zipDirectory + "/");
                            }
                        }
                    }
                    zipFilePath = zipfs.getPath(zipFilePath.toString(),inputFile.getName());
                    //zipFilePath.append(inputFile.getName());
                    //Path pathInZipfile = zipfs.getPath(zipFilePath.toString());
                    if (FilenameUtils.getExtension(inputFile.getName()).equals("xml")) {
                        zipFilePath = zipfs.getPath(zipfs.getRootDirectories().iterator().next().getRoot()
                                + inputFile.getName());
                    }
                    if (Files.notExists(zipFilePath)) {
                        Files.createDirectories(zipFilePath);
                    }
                    // copy a file into the zip file
                    Files.copy(inputFile.toPath(), zipFilePath,
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static void zipFilesDeprecated(File zipFile, List<File> files, boolean appendToFile) {
        try {
            if (files != null && !files.isEmpty()) {
                //now zip files one by one
                //create ZipOutputStream to write to the zip file
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile.getAbsoluteFile(), appendToFile);
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
                for (File file : files) {
                    logger.info("ZIP: " + file.getAbsolutePath());
                    //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                    //this should ALWAYS be forward slash, so replace any backslashes
                    String zipEntryPath = "";
                    String parentPath = zipFile.getParentFile().getAbsolutePath();
                    if (!file.getParentFile().getAbsolutePath().equals(parentPath)) {
                        zipEntryPath = file.getAbsolutePath().substring(parentPath.length() + 1).replaceAll("\\\\", "/");
                    }
                    ZipEntry ze = new ZipEntry(zipEntryPath);
                    zipOutputStream.putNextEntry(ze);
                    //read the file and write to ZipOutputStream
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                    zipOutputStream.closeEntry();
                    fis.close();
                }
                zipOutputStream.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void unzipFiles(File zipFile, File outputDirectory) {
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(outputDirectory, zipEntry);
                if (!zipEntry.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                //logger.info("UNZIP: " + newFile.getAbsolutePath());
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
