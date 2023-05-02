package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

public class ContentHelper {
    private static Logger logger = Logger.getLogger(ContentHelper.class);
    public static final Character[] INVALID_FILENAME_SPECIFIC_CHARS = {'"', '*', ':', '<', '>', '?', '\\', '|', 0x7F, '\000'};


    public static  int getCsvRowCount(File csvFile) {
        try {
            int rowCount = 0;
            CSVReader csvReader = new CSVReader(new FileReader(csvFile));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                rowCount = rowCount + 1;
            }
            return rowCount;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return  0;
        }
    }

    public static byte[] getFileBytes(File inputFile) {
        if (inputFile != null) {
            if (inputFile.exists()) {
                try {
                    return Files.readAllBytes(inputFile.toPath());
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public static String getFileContent(File inputFile) {
        if (inputFile != null) {
            if (inputFile.exists() && inputFile.length()!=0) {
                try {
                    logger.info("file content is not empty");
                    return new String(getFileBytes(inputFile));
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public static String getFileContent(String inputFilePath) {
        if (inputFilePath != null) {
            return getFileContent(new File(inputFilePath));
        }
        else
            return null;
    }


    public static Boolean isCSV(File inputFile) {
        try {
            CsvMapper mapper = new CsvMapper();
            mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);

            CsvMapper sourceMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<Map<String, String>> dataIterator = sourceMapper.readerFor(Map.class)
                    .with(schema)
                    .readValues(inputFile);

            Map<String, String> headerRow = dataIterator.next();

            return (headerRow != null) && (headerRow.size() > 0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public static Boolean isJSON(File inputFile) {
        try {
            JSONObject jsonObject = new JSONObject(getFileContent(inputFile));
        } catch (Exception eObject) {
            try {
                JSONArray jsonArray = new JSONArray(getFileContent(inputFile));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static boolean isUTF8(File inputFile) {

        byte[] pText = getFileBytes(inputFile);
        if (pText == null)
            return false;
        else {

            int expectedLength = 0;

            for (int i = 0; i < pText.length; i++) {
                if ((pText[i] & 0b10000000) == 0b00000000) {
                    expectedLength = 1;
                } else if ((pText[i] & 0b11100000) == 0b11000000) {
                    expectedLength = 2;
                } else if ((pText[i] & 0b11110000) == 0b11100000) {
                    expectedLength = 3;
                } else if ((pText[i] & 0b11111000) == 0b11110000) {
                    expectedLength = 4;
                } else if ((pText[i] & 0b11111100) == 0b11111000) {
                    expectedLength = 5;
                } else if ((pText[i] & 0b11111110) == 0b11111100) {
                    expectedLength = 6;
                } else {
                    return false;
                }

                while (--expectedLength > 0) {
                    if (++i >= pText.length) {
                        return false;
                    }
                    if ((pText[i] & 0b11000000) != 0b10000000) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static void makeDirectories(File directory) {
        try {
            if (directory != null && !directory.isFile() && !directory.exists() && !directory.getName().startsWith(".")) {
                //logger.info("MKDIR: " + directory);
                directory.mkdirs();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public static void writeFileContent(File outputFile, String fileContent) {
        try {
            writeFileContent(outputFile, fileContent.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public static void writeFileContent(File outputFile, byte[] fileContent) {
        try {
            if (outputFile != null && fileContent != null) {
                ContentHelper.makeDirectories(outputFile.getParentFile());
                Files.write(outputFile.toPath(), fileContent);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static String validateFileNameCharacters(String fileName) {
        String[] fn = new String[] {fileName};
        if (Arrays.stream(INVALID_FILENAME_SPECIFIC_CHARS)
                .anyMatch(ch -> fileName.contains(ch.toString()))) {
            Arrays.stream(INVALID_FILENAME_SPECIFIC_CHARS)
                    .forEach(ch -> {fn[0] = fn[0].replace(ch,'-');});
        }
        return fn[0];
    }
}
