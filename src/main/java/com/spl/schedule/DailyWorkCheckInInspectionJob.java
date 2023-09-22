package com.spl.schedule;

import com.spl.network.OkHttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

@Deprecated
public class DailyWorkCheckInInspectionJob {

    public static void startJob() throws IOException {

        String rawBody = "{\"month\":\"1664553600328\"}";
        Request request = new Request.Builder()
                .url("https://hr.sankuai.com/kaoqin/api/attendance/calendar")
                .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), rawBody))
                .addHeader("cookie", "_lxsdk_cuid=18112c99f6fc8-0e42719f6df17d-34736704-1ea000-18112c99f6fc8; moa_deviceId=A93B87C6C088595AB98D30003FE0118D; _ga=GA1.2.1132230060.1653877645; _lxsdk=sunpeilin; s_u_745896=ViNTgyxFQ578m0abZJqsqQ==; _gid=GA1.2.504496550.1666616353; al=bsmikllaodykmdfrxiwwmdrnmihqxtna; u=1080876289; uu=2e462930-5381-11ed-b4be-2187c4c38b0d; cid=1; _lx_utm=utm_source%3Dxm; s_m_id_3299326472=AwMAAAA5AgAAAAIAAAE9AAAALED52xMa0lnVbc2q/+YVoSWUrKXWACCKMGbsHaMP0O7vGIMClXzbq9fiKr4PAAAAIztRNZPoxCouf+mK77O/W492OtKg6l3l442yIX+WEb0CtD4Z; me_ssoid=eAGFzqtLQ2EYgHG-oTJMcpLxxO0Eeb_7-5nczhSjlyBY5Ludpv4DBqfJalMMioMFcWLTNBZsRoM2ES_HYBFBMImI2f7w8KuSsbfT60p68H73MABWWY2TaTBWWkSHEJmQzlhdYOaBQ2CeOovNG5LUl6Jb9HEtbjQMb6LOVQ6I0shG02CLAwCfmQZKsZW-fhx_XUGNsH_H-EOZGp69f-xfDmBuu3_72YMtko2OzC_k6yEmyVOnW54fPe-cvBy2y16nPGuPD6WbF3v12m-8S6p_sH0y4UThwAmjo6e68AWljlnJvTPSM-VxhSpFUSmhEBRfToXX2qCVOmRCBMaNYcFyUKEI0jLqvgEO-mad**eAENxscBwDAIBLCVTDPHOBSz_wiJXpLWeutzkM52wR7EPI4z921CLBHpf5uc1BBVoxvU-shCPjcREW4**kZkPGcDjeCx3QwnBNgPfpHU18TVhscX8ilBxfdjm2_whNcn_ko5vrNf3zcKWIJgicwwEmTJk3AuI7PApwynnlg**NTQ0NTM1NyxzdW5wZWlsaW4s5a2Z5Z-55p6XLHN1bnBlaWxpbkBtZWl0dWFuLmNvbSwxLDAzMjEzNjMwLDE2NjY5MjI3Mzk5OTY; webNewUuid=b4fb0b497ec17fcf11b2a53cb95c26c8_1661866468063,b4fb0b497ec17fcf11b2a53cb95c26c8_1661866468063; moaDeviceId=A93B87C6C088595AB98D30003FE0118D,A93B87C6C088595AB98D30003FE0118D; misId=sunpeilin; logan_session_token=2uehzo8fcz1hfb74orla; _lxsdk_s=184172e7f08-c40-b41-213%7C%7C22")
                .build();

        Response response = OkHttpUtil.request(request);
        if(Objects.nonNull(response) && response.isSuccessful()) {
            System.out.println(response.body().string());
        } else {
            System.out.println("code=" + response.code() + ", msg" + response.message());
        }


    }

}
