package com.spl.schedule;

import com.alibaba.fastjson.JSON;
import com.spl.domain.RailwayLeftTicketResponse;
import com.spl.domain.RailwaySeatType;
import com.spl.domain.RailwayTrainInfo;
import com.spl.email.EmailUtil;
import com.spl.network.OkHttpUtil;
import com.spl.util.FileUtil;
import com.spl.util.OsascriptUtil;
import com.spl.util.ThreadUtil;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RailwayTicketRemainMonitorJob {

    private static Long defaultSilenceTimeAddMonut = 50*1000L;   // 初始化1分钟静默
    private static final String BACK_ARG_FILE_PATH = "/Users/spl/autoTask/taskBackArg/taskArgs.txt";

    private static final SimpleDateFormat systemTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String leftTicketInfoUrl = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date={$Date}&leftTicketDTO.from_station=JNK&leftTicketDTO.to_station=BJP&purpose_codes=ADULT";

    // https://blog.csdn.net/feiyang5260/article/details/82255977
    public static void startJob() throws IOException {
        String cookies = "_jc_save_toDate=2023-10-06; ";
        String monitorDate = "2023-10-06";

        Request request = new Request.Builder().get()
                .url(leftTicketInfoUrl.replace("{$Date}", monitorDate))
                .addHeader("cookie", cookies)
                .build();
        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            System.out.println("请求成功，请求开始时间为" + systemTimeFormat.format(new Date()));
            RailwayLeftTicketResponse leftTicketResponse = JSON.parseObject(response.body().string(), RailwayLeftTicketResponse.class);
            List<String> queryResults = leftTicketResponse.getData().getResult();

            Map<String, String> aliasMap = leftTicketResponse.getData().getMap();
            for (String queryResult : queryResults) {
                RailwayTrainInfo trainInfo = RailwayTrainInfo.buildFromLeftTicketDataResult(queryResult, aliasMap);
                if(trainInfo == null || trainInfo.getSeatTypeNumMaps() == null
                        || trainInfo.getSeatTypeNumMaps().size() == 0) {
                    continue;
                }
                Map<RailwaySeatType, String> seatTypeNumMaps = trainInfo.getSeatTypeNumMaps();
                for (Map.Entry<RailwaySeatType, String> entry : seatTypeNumMaps.entrySet()) {
                    RailwaySeatType seatType = entry.getKey();
                    if(seatType.isDefaultFocus()) {
                        Long threadSilenceTime = ThreadUtil.CURRENT_THREAD_LOCAL.get();
                        threadSilenceTime = threadSilenceTime == null ? defaultSilenceTimeAddMonut:threadSilenceTime;

                        if( threadSilenceTime < 1800000) {
                            // 30分钟内，设置翻倍静默时间
                            threadSilenceTime = threadSilenceTime * 2;
                        }
                        FileUtil.writeToAppenderFile(BACK_ARG_FILE_PATH, System.currentTimeMillis() + " " + threadSilenceTime);

                        // 冲！
                        System.out.println("可售车票信息, 车次：" + trainInfo.getTrainNumber() + ", seatType=" + seatType.getName());
                        OsascriptUtil.triggerInfo("可售车票信息", "可售车次&座型",  trainInfo.getTrainNumber() + ":" + seatType.getName());
                        EmailUtil.sendTicketInfoEmilByJMail(trainInfo);
                        return;
                    }
                }
            }
            System.out.println("没有查询到任何可售车型");
            FileUtil.writeToAppenderFile(BACK_ARG_FILE_PATH, System.currentTimeMillis() + " " + defaultSilenceTimeAddMonut);  // 无票时重置静默
        }
    }

    @Deprecated
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
