package com.spl.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spl.network.OkHttpUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Deprecated
public class WatchPingAnBookingJob {

    public static void startJob() throws IOException {
        String url = "https://newretail.pingan.com.cn/ydt/booking/online/booking?time=" + System.currentTimeMillis();

        Map<String, String> param = new HashMap<>();
        param.put("contactName", "孙培林");
        param.put("contactTelephone", "17801007672");
        param.put("bookingSource", "miniApps");
        param.put("businessType", "14");
        param.put("storefrontSeq", "12836");
        param.put("detailaddress", "北京市朝阳区光华路5号院世纪财富中心2号楼2层（乘坐大厅扶梯至大厦二楼）");
        param.put("storefrontName", "平安产险北京分公司世纪财富中心门店");
        param.put("storefrontTelephone", "95511");
        param.put("deptCode", "201");
        param.put("agentFlag", "0");
        param.put("newCarFlag", "0");
        param.put("noPolicyFlag", "0");
        param.put("bookingType", "1");
        param.put("vehicleNo", "京B-B7F22");
        param.put("applicantIdCard", "");
        param.put("applicantName", "");
        param.put("longitude", "");
        param.put("latitude", "");
        param.put("businessKey", "");
        param.put("inputPolicyNo", "");
        param.put("vehicleNoEncode", "");

        Request request = new Request.Builder().url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("signature", "b0e6151453f1b8af314a1b8a667d0aed")
                .addHeader("sessionId", "19ae1530a2e04273ab22379c6e6901af8P58")
                .post(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.write(JSON.toJSONBytes(param));
                    }
                }).build();
        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            String bodyStr = response.body().string();
            System.out.println("请求成功，请求响应结果:" + bodyStr);

            JSONObject jsonObj = JSON.parseObject(bodyStr);
            if(jsonObj.get("limitTime") != null) {
                System.out.println(bodyStr);
            }


//            int code = (Integer) jsonObj.get("code");
//            if(code != 411 && jsonObj.get("data") != null ) {
//                // 411 : "LOGIN AUTHENTICATION FAILD"
//                //
//                System.out.println(bodyStr);
//            }
            return;
        }
        System.out.println("请求失败");
    }

}
