package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class ChecksumHelper {

    private static Logger logger = LogManager.getLogger(ChecksumHelper.class);

    public static String getHash(InputStream inputStream, String hashType) {
        try {
            MessageDigest md5er = MessageDigest.getInstance(hashType);
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = inputStream.read(buffer);
                if (read > 0)
                    md5er.update(buffer, 0, read);
            } while (read != -1);
            inputStream.close();
            byte[] digest = md5er.digest();
            if (digest == null)
                return null;
            StringBuilder checksum = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                checksum.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1).toLowerCase());
            }
            return checksum.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String getMd5(String txt) {
        try {
            return getHash(IOUtils.toInputStream(txt, StandardCharsets.UTF_8), "MD5");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String getMd5(File file) {
        try {
            return getHash(new FileInputStream(file), "MD5");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String getMd5(InputStream inputStream) {
        try {
            return getHash(inputStream, "MD5");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
