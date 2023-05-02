package com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.Set;

public class UtilHelper {

    public static boolean compare(Integer x, Integer y) {
        if (x != null && y != null) {
            return x.equals(y);
        }
        return false;
    }

    public static boolean compare(String x, String y) {
        if (x != null && y != null) {
            return x.equals(y);
        }
        return false;
    }

    public static boolean endsWith(File file, Set<String> fileExtensions) {
        if (file != null && fileExtensions != null && !fileExtensions.isEmpty()) {
            for (String fileExtension : fileExtensions) {
                fileExtension = fileExtension.replace("*.", ".");

                if (file.getName().toLowerCase().endsWith(fileExtension)) {
                    return true;
                }
                else if (fileExtension.equals("*") || fileExtension.equals("*.*")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean toBoolean(Object value) {
        if (value != null) {
            if (NumberUtils.isCreatable(value.toString())) {
                return Double.parseDouble(value.toString()) > 0;
            }
            if (value.toString().equalsIgnoreCase("yes")
                    || value.toString().equalsIgnoreCase("y")) {
                return true;
            }
            return BooleanUtils.toBoolean(value.toString().toUpperCase());
        }
        return null;
    }

}
