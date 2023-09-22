package com.spl.email;

public interface EmailConstants {

    String certificateEmailTemplate = "证书查询消息通知" +
            "最新证书消息：{itemName}" +
            "最新证书时间：{itemTime}";

    String SMTP_163_HOST = "smtp.163.com";

    String SMTP_TRANSPORT_TYPE = "smtp";

    String PERSONAL_EMAIL_USER = "孙培林的自动化任务";

    String PERSONAL_EMAIL_NAME = "plsun1108@163.com";

    String PERSONAL_EMAIL_AUTH_CODE = "EBBFKLGPMMORNTSV";

}
