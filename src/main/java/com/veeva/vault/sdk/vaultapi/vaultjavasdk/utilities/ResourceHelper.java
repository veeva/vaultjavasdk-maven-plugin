package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class ResourceHelper {
    private static final Logger logger = LogManager.getLogger(ResourceHelper.class);
    private static Path runPath;

    public static void setRunPath(Path runPath) {
        ResourceHelper.runPath = runPath;
    }


        public static File getFile(String filePath) {
        if (filePath != null) {
            return Paths.get(filePath).toFile();
        } else {
            return null;
        }
    }


    public static List<String> getFileNames(File sourceFile, Set<String> fileExtensions) {
        try (Stream<Path> walk = Files.walk(sourceFile.toPath())) {

            return walk.map(x -> x.toString())
                    .filter(file -> (fileExtensions == null
                            || UtilHelper.endsWith(new File(file), fileExtensions)))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<String> getFileNames(File sourceFile, String fileExtension) {
        try (Stream<Path> walk = Files.walk(sourceFile.toPath())) {

            Set<String> fileExtensions = null;
            if (fileExtension != null) {
                fileExtensions = Collections.singleton(fileExtension);
            }
            return getFileNames(sourceFile, fileExtensions);

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<String> getFileNames(File sourceFile) {
        try (Stream<Path> walk = Files.walk(sourceFile.toPath())) {

            Set<String> fileExtensions = null;
            return getFileNames(sourceFile, fileExtensions);

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<File> getFiles(File source, Set<String> fileExtensions) {
        try {
            List<File> files = new ArrayList<>();
            if (source.isDirectory()) {
                List<String> fileNames = ResourceHelper.getFileNames(source, fileExtensions);
                assert fileNames != null;
                Collections.sort(fileNames);
                for (String filePath : fileNames) {
                    files.add(new File(filePath));
                }
            } else {
                files.add(source);
            }

            return files;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<File> getFiles(File sourceFile, String fileExtension) {
        try {
            Set<String> fileExtensions = null;
            if (fileExtension != null) {
                fileExtensions = Collections.singleton(fileExtension);
            }
            return getFiles(sourceFile, fileExtensions);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<File> getFiles(File sourceFile) {
        try {
            Set<String> fileExtensions = null;
            return getFiles(sourceFile, fileExtensions);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void copy(Path source, Path dest, boolean logActivity) {
        try {
            logger.info("CREATE: " + dest.toAbsolutePath());
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);


        }
    }

    public static String localToFullPath(String path) {
        if (path != null && runPath != null) {
            if (path.equals(".")) {
                path = "";
            }
            if (path.startsWith("/")) {
                return path;
            }
            else if (path.contains(":\\")) {
                return path;
            }
            else {
                return Paths.get(runPath.toString(), path).toString();
            }
        }
        else {
            return path;
        }
    }
}
