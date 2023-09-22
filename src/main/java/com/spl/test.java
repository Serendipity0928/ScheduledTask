package com.spl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    private static final Pattern pattern = Pattern.compile(".*<record><!\\[CDATA\\[(.*)</table>\\]\\]></record>.*");
    private static final Pattern pattern1 = Pattern.compile("<record>(.*?)</record>");

    public static void main(String[] args) {
        String line = "ss<record>td</record>aaaa<record>td>    </tr></table>]]></record>s";

        Matcher matcher = pattern1.matcher(line);
        int start = 0;
        while (matcher.find(start)) {
            System.out.println(matcher.group(1));
            start = matcher.end(1);
        }

        if(start == 0) {
            System.out.println("没有找到有效数据！！line="+line);
        }
    }

}
