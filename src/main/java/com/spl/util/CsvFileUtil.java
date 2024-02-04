package com.spl.util;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFileUtil {

    public static void creatCsvFile(List<String> contents) {
        String csvFile = "data.csv"; // CSV文件路径

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            String[] header = {"联系人", "手机1", "手机2", "手机3", "手机4", "手机5", "手机6", "手机7", "手机8", "手机9"}; // 表头
            writer.writeNext(header); // 写入表头

            for (int i = 0; i < ((contents.size() / 10)); i++) {
                // 最后几个舍弃了
                int base = i*10;
                String[] row = new String[11];
                row[0] = "名" + i;
                for (int j = 1; j < 11; j++) {
                    row[j] = contents.get(base + j - 1);
                }
                writer.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
