package com.chenyihong.plugin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static void log(String message) {
        System.out.println("example-plugin-log:" + message);
    }

    public static String replaceStringBlank(String originString) {
        String replaceString = "";
        if (isEmpty(originString)) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(originString);
            replaceString = matcher.replaceAll("");
        }
        return isEmpty(replaceString) ? originString : replaceString;
    }

    public static boolean isEmpty(String stringValue) {
        return stringValue == null || stringValue.isEmpty() || stringValue.isBlank();
    }

    public static String getImageType(String imagePath) {
        try {
            String fileType = "";
            if (imagePath.endsWith("jpg") || imagePath.endsWith("jpeg")) {
                fileType = "jpg";
            }
            if (imagePath.endsWith("webp")) {
                fileType = "webp";
            }
            if (imagePath.endsWith("png")) {
                fileType = "png";
            }
            if (imagePath.endsWith("gif")) {
                fileType = "gif";
            }
            log("imagePath:" + imagePath + ", fileType:" + fileType);
            return fileType;
        } catch (Exception e) {
            log("get image type failed due to " + e.getMessage());
            return "";
        }
    }

    public static void saveFileToTargetFolder(File parentFile, String fileName, byte[] fileContentDataBytes, boolean alwaysRecreate) {
        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            log("create parent file :" + mkdirs);
        }
        File createFile = new File(parentFile, fileName);

        log("create file path :" + createFile.getPath());

        if (createFile.exists()) {
            if (!alwaysRecreate) {
                return;
            }
            boolean delete = createFile.delete();
            log("delete old file success :" + delete);
        }

        try {
            boolean create = createFile.createNewFile();
            log("create new file success :" + create);
        } catch (IOException e) {
            log("create new file failure errorMessage:" + e.getMessage());
        }

        if (createFile.exists()) {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(createFile);
                outputStream.write(fileContentDataBytes);
            } catch (IOException e) {
                log("write file failure errorMessage:" + e.getMessage());
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    log("close stream failure errorMessage:" + e.getMessage());
                }
            }
        }
    }

    public static void writeDataToFile(File targetFile, String data) {
        if (targetFile.getParentFile().mkdirs()) {
            log("create target file's parent file succeed");
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
            fileWriter.write(data);
        } catch (Exception e) {
            log("write data to target file failed due to " + e.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                log("close stream failed due to " + e.getMessage());
            }
        }
    }
}
