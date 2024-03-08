package temp;

import org.junit.Test;

public class LeakBucketLimiterTest {

    @Test
    public void testFun() {
        long startTime = 1709740800000L;
        long endTime = 1709740800500L;
        int rate = 2;   // 1s -2个

        long l = ((endTime - startTime) / 1000) * rate;
        System.out.println(l);

    }

}
