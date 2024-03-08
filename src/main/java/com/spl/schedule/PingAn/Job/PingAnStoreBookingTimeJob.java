package com.spl.schedule.PingAn.Job;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.spl.domain.PingAnApiResponse;
import com.spl.network.OkHttpUtil;
import com.spl.schedule.PingAn.exception.PingAnBusinessException;
import com.spl.util.JsonUtils;
import com.spl.util.PrintUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PingAnStoreBookingTimeJob {

    /**
     * 1. 成功响应：{"code":200,"msg":"OK","data":[{"storefrontSeq":"12836","bookingDate":"2024年03月08日 星期五","businessType":"03","totalBookableNum":47,"totalBookable":50,"totalBooked":3,"bookingRules":[{"idBookingSurvey":"2bcedb0bd0c448d7b8446e4b2ba2db51","startTime":"9:30","endTime":"16:30","bookableNum":47,"bookedNum":3}]}]}
     * 2. {"code":309,"msg":"SIGNATURE ILLEGAL","data":null}   signature未传或不合法
     * 3. {"code":411,"msg":"LOGIN AUTHENTICATION FAILD","data":null}  sessionId未传或不合法
     * 4. {"code":-5,"msg":"参数不合法 fileNa...","data":null}    缺少请求参数
     * 5. {"code":411,"msg":"LOGIN AUTHENTICATION FAILD","data":null}      登陆认证过期
     */
    private static final Set<Integer> expectedResponseCodes = Sets.newHashSet(200);

    private PingAnConfig bookConfig;

    private String requestURL;

    public PingAnStoreBookingTimeJob(PingAnConfig bookConfig) {
        if(!checkBookingConfig(bookConfig)) {
            log.error("查询平安预约时间，配置不足, config={}", bookConfig);
            throw new PingAnBusinessException("查询平安预约时间，配置不足");
        }

        this.bookConfig = bookConfig;

        requestURL = "https://newretail.pingan.com.cn/ydt/reserve/store/bookingTime?" +
                "storefrontseq=" + bookConfig.getStorefrontseq() +
                "&businessType=" + bookConfig.getBusinessType() +
                "&source=miniApps" +
                "&time=" + System.currentTimeMillis();

    }

    private boolean checkBookingConfig(PingAnConfig bookConfig) {
        return Objects.nonNull(bookConfig)
                && StringUtils.isNoneBlank(
                        bookConfig.getSessionId(), bookConfig.getSignature(),
                        bookConfig.getStorefrontseq(), bookConfig.getBusinessType()
                );
    }

    /**
     * 任务运行结果
     * 非空list：运行成功，继续任务
     * 空list：需重试当前任务
     * 其他：终止任务
     */
    public Optional<List<BookingTimeBO>> requestAPI() throws IOException {

        Request request = new Request.Builder()
                .get().url(requestURL)
                .addHeader("signature", this.bookConfig.getSignature())
                .addHeader("sessionId", this.bookConfig.getSessionId())
                .build();

        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            String bodyStr = response.body().string();

            PingAnApiResponse<List<BookingTimeData>> apiResponse = JsonUtils.parseObject(bodyStr, new TypeReference<PingAnApiResponse<List<BookingTimeData>>>() {});
            if(apiResponse == null || !expectedResponseCodes.contains(apiResponse.getCode())) {
                // 非预期code，打印详细日志，程序终止，后续排查优化【暂无重试】
                log.error("非预期响应或预期code, apiResponse={}, 程序终止！", apiResponse);
                return Optional.empty();
            }

            if(apiResponse.getCode() == 200) {
                // 成功响应
                // 尝试将请求的数据转换为业务数据，存在业务需要的数据，算是成功，否则应重试当前请求
                List<BookingTimeBO> bookingTimeBOS = covertApiResponseDataToBO(apiResponse.getData());
                if(CollectionUtils.isEmpty(bookingTimeBOS)) {
                    // 无数据，重试 【随机打印日志】
                    return Optional.of(Collections.emptyList());
                }

                // 如何把数据交给其他任务？
                log.info("已获取到数据，bookingTimeBOS={}", bookingTimeBOS);
                return Optional.of(bookingTimeBOS);
            }
        }

        log.error("请求失败，API响应为空, 程序终止！response={}, body={}", response, response.body());

//        return Optional.empty();  // 为啥
        /**
         * com.spl.schedule.PingAn.Job.PingAnStoreBookingTimeJob - 请求失败，API响应为空, 程序终止！response=Response{protocol=http/1.1, code=503, message=Service Temporarily Unavailable
         */
        return Optional.of(Collections.emptyList());
    }

    private static List<BookingTimeBO> covertApiResponseDataToBO(List<BookingTimeData> bookingTimeDataList) {
        if(CollectionUtils.isEmpty(bookingTimeDataList)) {
            return Collections.emptyList();
        }

        return bookingTimeDataList.stream()
                .filter(list -> CollectionUtils.isNotEmpty(list.getBookingRules()))
                .flatMap(bookingTimeData -> bookingTimeData.getBookingRules().stream()
                        .filter(r -> r.getBookableNum() > r.getBookedNum())
                        .map(bookingRules -> {
                            BookingTimeBO bookingTimeBO = new BookingTimeBO();
                            bookingTimeBO.setStorefrontSeq(bookingTimeData.getStorefrontSeq());
                            bookingTimeBO.setBookingDate(bookingTimeData.getBookingDate());
                            bookingTimeBO.setBusinessType(bookingTimeData.getBusinessType());
                            bookingTimeBO.setIdBookingSurvey(bookingRules.getIdBookingSurvey());
                            bookingTimeBO.setStartTime(bookingRules.getStartTime());
                            bookingTimeBO.setEndTime(bookingRules.getEndTime());
                            return bookingTimeBO;
                        }))
                .collect(Collectors.toList());
    }


    @Data
    public static class BookingTimeData {

        private String storefrontSeq;       // 店标识  focus

        private String bookingDate;         // 预约日期  focus

        private String businessType;        // 业务类型  focus

        private int totalBookableNum;

        private int totalBookable;

        private int totalBooked;

        private List<BookingRules> bookingRules;        // 预约规则  focus

    }

    @Data
    public static class BookingRules{

        private String idBookingSurvey;     // 预约场次ID  focus

        private String startTime;           // 当前场次开始时间  focus

        private String endTime;             // 当前场次结束时间  focus

        private int bookableNum;            // 当前场次可预约的数量  focus

        private int bookedNum;              // 当前场次已预约的数量  focus

    }

    @Data
    public static class BookingTimeBO{

        private String storefrontSeq;       // 店标识

        private String bookingDate;         // 预约日期

        private String businessType;        // 业务类型

        private String idBookingSurvey;     // 预约场次ID

        private String startTime;           // 当前场次开始时间

        private String endTime;             // 当前场次结束时间
    }


}
