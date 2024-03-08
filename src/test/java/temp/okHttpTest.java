package temp;

import okhttp3.Request;

public class okHttpTest {
    public static void main(String[] args) {
        Request request = new Request.Builder().url("https://www.baidu.com/").build();
        System.out.println(request);
    }
}
