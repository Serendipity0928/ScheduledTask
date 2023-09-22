package com.spl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailContentBO {

    private String emailHost;               //发送邮件的主机
    private String transportType;           //邮件发送的协议
    private String fromUser;                //发件人名称
    private String fromEmail;               //发件人邮箱
    private String authCode;                //发件人邮箱授权码
    private String toEmail;                 //收件人邮箱
    private String subject;                 //主题信息

    private String emailContent;

}
