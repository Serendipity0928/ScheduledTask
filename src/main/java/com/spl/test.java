package com.spl;

import com.alibaba.fastjson2.JSON;
import com.spl.domain.PhoneDistrictResponse;
import com.spl.network.OkHttpUtil;
import com.spl.util.CsvFileUtil;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class test {

    private static final String[] random2Str = new String[100];

    static {
        for (int i = 0; i < 100; i++) {
            if(i >= 10) {
                random2Str[i] = String.valueOf(i);
            } else {
                random2Str[i] = "0" + i;
            }
        }
    }

    public static void main(String[] args) throws IOException {
    //   https://blog.csdn.net/zhangchb/article/details/106072142

        for (int i = 1; i < 2; i++) {
            final int j = i;
            new Thread(()-> {
                try {
                    task(1000*j, 1000 * (j+1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void task(int start, int end) throws IOException {
        List<String> medCandidates = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        String medStr = "";
        for (int i = start; i < end; i++) {
            if(i >= 1000) {
                medStr = String.valueOf(i);
            } else if(i >=100) {
                medStr = "0" + i;
            } else if(i >= 10) {
                medStr = "00" + i;
            } else {
                medStr = "000" + i;
            }

            try {
                String url = "https://cx.shouji.360.cn/phonearea.php?number=176" + medStr + "0070";
                Request request = new Request.Builder().get()
                        .url(url)
                        .build();
                Response response = OkHttpUtil.request(request);
                if(Objects.nonNull(response) && response.isSuccessful()) {
                    String string = response.body().string();
                    PhoneDistrictResponse phoneRes = JSON.parseObject(string, PhoneDistrictResponse.class);
                    if(phoneRes.getData().getProvince().equals("北京")) {
                        medCandidates.add(medStr);
                    }
                }
            } catch (Exception e) {
                failures.add(medStr);
                if (failures.size() > 1000) {
                    System.out.println("错误太多了...");
                    throw new RuntimeException("错误太多了");
                }
            }
        }

        System.out.println("查询个数：" + medCandidates.size());
        System.out.println(medCandidates);
        System.out.println(failures);


        List<String> collect = medCandidates.stream()
                .flatMap(med ->
                        Arrays.stream(random2Str)
                                .map(k -> "176" + med + k + "70"))
                .collect(Collectors.toList());
        CsvFileUtil.creatCsvFile(collect);
    }




}
