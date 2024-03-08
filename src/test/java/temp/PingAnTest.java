package temp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spl.schedule.WatchPingAnBookingJob;
import org.junit.Test;

import java.io.IOException;

public class PingAnTest {

    @Test
    public void testJobThread() {

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    WatchPingAnBookingJob.startJob();
                } catch (IOException e) {
                    System.out.println("异常。。。");
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    WatchPingAnBookingJob.startJob();
                } catch (IOException e) {
                    System.out.println("异常。。。");
                }
            }
        }).start();
    }

    @Test
    public void testJob() throws IOException {
        WatchPingAnBookingJob.startJob();
    }

    @Test
    public void testJson() {
        String bodyStr = "{\"code\":411,\"msg\":\"LOGIN AUTHENTICATION FAILD\",\"data\":null}";
        JSONObject jsonObj = JSON.parseObject(bodyStr);
        int code = (Integer) jsonObj.get("code");
        System.out.println(code);


        Object data = jsonObj.get("data");
        if(data != null) {
            System.out.println(bodyStr);
        }

    }

}
