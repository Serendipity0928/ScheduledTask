package com.spl.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtil {

    public static void writeToAppenderFile(String pathName, String appendContent) {
        if (StringUtils.isBlank(appendContent)) {
            return;
        }

        File file = new File(pathName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
            bw.newLine();
            bw.write(appendContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
