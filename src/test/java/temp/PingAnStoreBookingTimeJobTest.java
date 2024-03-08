package temp;

import com.alibaba.fastjson.TypeReference;
import com.spl.domain.PingAnApiResponse;
import com.spl.schedule.PingAn.Job.PingAnStoreBookingTimeJob;
import com.spl.util.JsonUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class PingAnStoreBookingTimeJobTest {

    @Test
    public void testAPI() throws IOException {

//        PingAnStoreBookingTimeJob.startJob();

    }

    @Test
    public void testAPIResponse() {

        String successRes = "{\"code\":200,\"msg\":\"OK\",\"data\":[{\"storefrontSeq\":\"12836\",\"bookingDate\":\"2024年03月08日 星期五\",\"businessType\":\"03\",\"totalBookableNum\":47,\"totalBookable\":50,\"totalBooked\":3,\"bookingRules\":[{\"idBookingSurvey\":\"2bcedb0bd0c448d7b8446e4b2ba2db51\",\"startTime\":\"9:30\",\"endTime\":\"16:30\",\"bookableNum\":47,\"bookedNum\":3}]}]}";
        String illegalRes = "{\"code\":309,\"msg\":\"SIGNATURE ILLEGAL\",\"data\":null}";

        final String bodyStr = successRes;
        PingAnApiResponse<List<PingAnStoreBookingTimeJob.BookingTimeData>> obj = JsonUtils.parseObject(bodyStr, new TypeReference<PingAnApiResponse<List<PingAnStoreBookingTimeJob.BookingTimeData>>>() {});
        System.out.println(obj);
    }

}
