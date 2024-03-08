package com.spl.schedule.PingAn.Job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spl.network.OkHttpUtil;
import com.spl.schedule.PingAn.exception.PingAnBusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ReservePingAnOffline {

    private PingAnConfig bookConfig;

    private String requestURL;

    private Map<String, Object> requestBody;

    public ReservePingAnOffline(PingAnConfig bookConfig) {
        if(!checkBookingConfig(bookConfig)) {
            log.error("执行平安预约，配置不足, config={}", bookConfig);
            throw new PingAnBusinessException("执行平安预约，配置不足");
        }

        this.bookConfig = bookConfig;

        requestURL = "https://newretail.pingan.com.cn/ydt/reserve/reserveOffline?time=" + System.currentTimeMillis();

        requestBody = new HashMap<>();
        requestBody.put("businessName", "办理退保");     // option  应该是啥
        requestBody.put("storefrontName", "平安产险北京分公司世纪财富中心门店"); // option
//        requestBody.put("bookingDate", "2024年03月08日 星期五");    // required
//        requestBody.put("bookingTime", "9:30-16:30");     // required
        requestBody.put("bookingType", "1");      // required
        requestBody.put("bookContent", "");
//        requestBody.put("storefrontseq", "12836");        // required
        requestBody.put("storefrontTelephone", "95511");
//        requestBody.put("businessType", "03");    // 14  // required
        requestBody.put("businessKey", null);
//        requestBody.put("idBookingSurvey", "2bcedb0bd0c448d7b8446e4b2ba2db51");    // required
        requestBody.put("detailaddress", "北京市朝阳区光华路5号院世纪财富中心2号楼2层（乘坐大厅扶梯至大厦二楼）");  // option
        requestBody.put("deptCode", "201");
        requestBody.put("contactName", "孙培林");
        requestBody.put("contactTelephone", "17801007672");  // required
        requestBody.put("applicantName", "");
        requestBody.put("applicantIdCard", "");
        requestBody.put("bookingSource", "miniApps");     // required
        requestBody.put("agentFlag", "0");
        requestBody.put("newCarFlag", "0");
        requestBody.put("noPolicyFlag", "0");
        requestBody.put("vehicleNo", "京B-B7F22");  // 22  // required
        requestBody.put("longitude", "");
        requestBody.put("latitude", "");
        requestBody.put("inputPolicyNo", "");
        requestBody.put("smsCode", "");
        requestBody.put("offlineItemList", Collections.emptyList());
    }

    private boolean checkBookingConfig(PingAnConfig bookConfig) {
        return Objects.nonNull(bookConfig)
                && StringUtils.isNoneBlank(
                bookConfig.getSessionId(), bookConfig.getSignature(),
                bookConfig.getStorefrontseq(), bookConfig.getBusinessType()
        );
    }

    public void addRuntimeBody(PingAnStoreBookingTimeJob.BookingTimeBO currentBookItem) {
        requestBody.put("bookingDate", currentBookItem.getBookingDate());    // required
        requestBody.put("bookingTime", currentBookItem.getStartTime() + "-" + currentBookItem.getEndTime());     // required
        requestBody.put("storefrontseq", currentBookItem.getStorefrontSeq());        // required
        requestBody.put("businessType", currentBookItem.getBusinessType());    // 14  // required
        requestBody.put("idBookingSurvey", currentBookItem.getIdBookingSurvey());    // required
    }


    public Optional<Boolean> requestAPI() throws IOException {
        Request request = new Request.Builder().url(requestURL)
                .addHeader("Content-Type", "application/json")
                .addHeader("signature", this.bookConfig.getSignature())
                .addHeader("sessionId", this.bookConfig.getSessionId())
                .post(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.write(JSON.toJSONBytes(requestBody));
                    }
                }).build();
        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            String bodyStr = response.body().string();

            /**
             * 成功响应：{"code":0,"msg":"success","data":null}
             * {"code":-13010,"msg":"当前日期不支持预约或已约满","data":null}：先校验了bookingDate、bookingTime参数
             * {"code":-1501,"msg":"接口请求失败","data":null}：idBookingSurvey传的不对
             *
             * {"code":-13004,"msg":"同一日期用户不能同时预约多个未完成的订单","data":null}：已预约成功
             *
             */

            JSONObject jsonObj = JSON.parseObject(bodyStr);
            int code = jsonObj.get("code") != null ? (Integer) jsonObj.get("code") : -100;
            if(code == 0) {
                log.info("成功预约");
                return Optional.of(Boolean.TRUE);
            } else if(code == -13004 ) {
                log.error("已预约成功，终止, bodyStr={}", bodyStr);
                return Optional.of(Boolean.TRUE);
            } else if(code == -13010 ){
                log.error("已约满，终止, bodyStr={}", bodyStr);
                return Optional.of(Boolean.TRUE);
            }

            return Optional.empty();
        }
        log.error("请求失败，请重试");
        return Optional.empty();
    }

}
