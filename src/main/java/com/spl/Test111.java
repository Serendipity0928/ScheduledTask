package com.spl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test111 {

    private static final Pattern certificateNamePattern = Pattern.compile("title=(.*?)>");
    private static final Pattern certificateTimePattern = Pattern.compile(">\\[(.*?)]<");

    private static final String Item = "<![CDATA[<tableborder=0cellpadding=0cellspacing=0height=29style=border-bottom:1pxdashed#CDCDCD;width=675><tr><tdwidth=10></td><tdalign=leftclass=bt_timestyle=font-weight:bolder;color:#CDCDCD;valign=middlewidth=15>·</td><tdalign=leftheight=27width=540><aclass=font14heihref=/art/2019/3/1/art_41090_4568065.htmltarget=_blanktitle=全国专业技术人员职业资格证书邮寄服务须知>全国专业技术人员职业资格证书邮寄服务须知</a></td><tdalign=rightwidth=115><spanstyle=font-size:12px;line-height:180%;>[2019-03-01]</span></td><tdwidth=10></td></tr></table>]]>";
    private static final String Item1 = ">[2019-03-01]<";

    public static void main(String[] args) {
        Matcher matcher = certificateTimePattern.matcher(Item1);
        int matcher_start = 0;
        while (matcher.find(matcher_start)){
            System.out.println(matcher.group(1));
            matcher_start = matcher.end();
        }
    }
}
