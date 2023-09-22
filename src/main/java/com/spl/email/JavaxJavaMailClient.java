package com.spl.email;

import com.spl.domain.EmailContentBO;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class JavaxJavaMailClient {

    public static void sendEmail(EmailContentBO emailContentBO) throws javax.mail.MessagingException {

        //初始化默认参数
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", emailContentBO.getTransportType());
        props.setProperty("mail.host", emailContentBO.getEmailHost());
        props.setProperty("mail.user", emailContentBO.getFromUser());
        props.setProperty("mail.from", emailContentBO.getFromEmail());
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.debug", "false");
        //获取Session对象
        Session session = Session.getInstance(props, null);
        //开启后有调试信息
//        session.setDebug(true);

        //通过MimeMessage来创建Message接口的子类
        MimeMessage message = new MimeMessage(session);
        //下面是对邮件的基本设置
        //设置发件人：
        //设置发件人第一种方式：直接显示：antladdie <antladdie@163.com>
        //InternetAddress from = new InternetAddress(sender_username);
        //设置发件人第二种方式：发件人信息拼接显示：蚂蚁小哥 <antladdie@163.com>
//        String formName = MimeUtility.encodeWord("监控小哥") + " <" + fromEmail + ">";
        InternetAddress from = new InternetAddress(emailContentBO.getFromEmail());
        message.setFrom(from);

        //设置收件人：
        InternetAddress to = new InternetAddress(emailContentBO.getToEmail());
        message.setRecipient(Message.RecipientType.TO, to);

        //设置抄送人(两个)可有可无抄送人：
//        List<InternetAddress> addresses = Arrays.asList(new InternetAddress("1457034247@qq.com"), new InternetAddress("575814158@qq.com"));
//        InternetAddress[] addressesArr = (InternetAddress[]) addresses.toArray();
//        message.setRecipients(Message.RecipientType.CC, addressesArr);

        //设置密送人 可有可无密送人：
        //InternetAddress toBCC = new InternetAddress(toEmail);
        //message.setRecipient(Message.RecipientType.BCC, toBCC);

        //设置邮件主题
        message.setSubject(emailContentBO.getSubject());

        //设置邮件内容,这里我使用html格式，其实也可以使用纯文本；纯文本"text/plain"
        message.setContent(emailContentBO.getEmailContent(), "text/html;charset=UTF-8");

        //保存上面设置的邮件内容
//        message.saveChanges();

        //获取Transport对象
        Transport transport = session.getTransport();
        //smtp验证，就是你用来发邮件的邮箱用户名密码（若在之前的properties中指定默认值，这里可以不用再次设置）
        transport.connect(emailContentBO.getFromEmail(), emailContentBO.getAuthCode());
        //发送邮件
        transport.sendMessage(message, message.getAllRecipients()); // 发送
    }
}
