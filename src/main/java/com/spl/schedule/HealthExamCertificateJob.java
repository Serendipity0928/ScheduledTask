package com.spl.schedule;

import com.spl.email.EmailUtil;
import com.spl.network.OkHttpUtil;
import com.spl.util.OsascriptUtil;
import com.spl.util.TimeSpanControlUtil;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 济南人事考试证书办理项监控
 */
public class HealthExamCertificateJob {

    private static final SimpleDateFormat certificateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat systemTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String lastedItemTime = "2022-9-29";

    private static final Pattern certificateItemPattern = Pattern.compile("<record>(.*?)</record>");
    private static final Pattern certificateNamePattern = Pattern.compile("title=(.*?)>");
    private static final Pattern certificateTimePattern = Pattern.compile(">\\[(.*?)]<");

    public static void startJob() throws IOException {
        if(!TimeSpanControlUtil.healthExamScheduleTime()) {
            return;
        }

        Request request = new Request.Builder().get().url("http://jnhrss.jinan.gov.cn/col/col41090/index.html").build();

        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            System.out.println("请求成功，请求开始时间为" + systemTimeFormat.format(new Date()));
            String body = response.body().string().replace("\n", "").replace(" ", "").replace("\"", "");
            Matcher matcher = certificateItemPattern.matcher(body);

            try {
                int count = 0;
                int matcher_start = 0;
                while (matcher.find(matcher_start)) {
                    String infoItem = matcher.group(1);

                    Matcher nameMatcher = certificateNamePattern.matcher(infoItem);
                    Matcher timeMatcher = certificateTimePattern.matcher(infoItem);
                    if(nameMatcher.find() && timeMatcher.find()) {
                        String itemName = nameMatcher.group(1);
                        String itemTime = timeMatcher.group(1);

                        Date curDate = certificateTimeFormat.parse(itemTime);
                        if(curDate.after(certificateTimeFormat.parse(lastedItemTime))) {
                            System.out.println("有新消息提醒, itemName=" +itemName + ", itemName=" + itemTime);
                            OsascriptUtil.triggerInfo("济南人事考试中心", "证书查询结果", itemName);
                            EmailUtil.sendCertificateEmilByJMail(itemName, itemTime);
                            break;
                        }
                    } else {
                        System.out.println("没有找到证书办理项的名称或时间");
                        break;
                    }
                    matcher_start = matcher.end();
                    count++;
                }
                System.out.println("共查询到证书发布项相关个数为"+count);
            } catch (Exception e) {
                System.out.println("出现异常");
            }
        }
        System.out.println("结束查找，请求结束时间为" + systemTimeFormat.format(new Date()));
    }

}
