package com.spl.schedule;

import com.alibaba.fastjson.JSON;
import com.spl.domain.RailwayLeftTicketResponse;
import com.spl.domain.RailwayTrainInfo;
import com.spl.network.OkHttpUtil;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RailwayTicketRemainMonitorJob {

    private static final SimpleDateFormat systemTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String leftTicketInfoUrl = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=2023-09-22&leftTicketDTO.from_station=JNK&leftTicketDTO.to_station=BJP&purpose_codes=ADULT";

    // https://blog.csdn.net/feiyang5260/article/details/82255977
    public static void startJob() throws IOException {

//        String cookies = getCookies();
//        if(StringUtils.isBlank(cookies)) {
//            return;
//        }
        String cookies = "_jc_save_toDate=2023-09-22; ";

        Request request = new Request.Builder().get()
                .url(leftTicketInfoUrl)
                .addHeader("cookie", cookies)
                .build();
        System.out.println(cookies);
        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            System.out.println("请求成功，请求开始时间为" + systemTimeFormat.format(new Date()));
            RailwayLeftTicketResponse leftTicketResponse = JSON.parseObject(response.body().string(), RailwayLeftTicketResponse.class);
            List<String> queryResults = leftTicketResponse.getData().getResult();
            System.out.println(queryResults.get(0));
//            String queryResult = queryResults.get(0);
//            System.out.println(queryResult);
//            int length = queryResult.split("\\|").length;
//            System.out.println(length);
//            for (String queryResult1 : queryResults) {
//                assert queryResult1.split("\\|").length == length;
//            }

            Map<String, String> aliasMap = leftTicketResponse.getData().getMap();

            for (String queryResult : queryResults) {
//                RailwayTrainInfo.buildFromLeftTicketDataResult(queryResult, aliasMap)

            }
//            RailwayTrainInfo.buildFromLeftTicketDataResult()


        }



    }

    private static String getCookies() {
        Request request = new Request.Builder().get().url("https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc&fs=%E6%B5%8E%E5%8D%97,JNK&ts=%E5%8C%97%E4%BA%AC,BJP&date=2023-09-22&flag=N,N,Y").build();
        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            System.out.println("请求成功，请求开始时间为" + systemTimeFormat.format(new Date()));
            Headers headers = response.headers();
            List<String> cookies = headers.values("Set-Cookie");
            if (cookies.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : cookies) {
                    sb.append(s);
                }
                return sb.toString();
            }
        }
        return "";
    }

}
