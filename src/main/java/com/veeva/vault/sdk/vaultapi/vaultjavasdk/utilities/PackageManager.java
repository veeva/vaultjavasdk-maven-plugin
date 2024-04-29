package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageManager {

	private static final Logger logger = LogManager.getLogger(PackageManager.class);

	public static boolean cleanPackageDirectory() {

		try {
			Stream<Path> fileWalk = Files.walk(Paths.get("", "deployment"));
			List<Path> fileList = fileWalk.filter(pp -> !Files.isDirectory(pp)).collect(Collectors.toList());

			fileList.forEach(p -> {
					try {
						Files.deleteIfExists(p);
					} catch (IOException e) {
						logger.error("An error has occurred. " + e.getMessage());
					}
			});
			fileWalk.close();

		} catch (IOException e) {
			logger.error("An error has occurred. " + e.getMessage());
			return false;
		}
		return true;
	}
	
}

