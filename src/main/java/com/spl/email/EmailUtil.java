package com.spl.email;

import com.spl.domain.EmailContentBO;

import javax.mail.MessagingException;

public class EmailUtil {

    public static void sendCertificateEmilByJMail(String itemName, String itemTime) {

        String emailContent = EmailConstants.certificateEmailTemplate
                .replace("{itemName}", itemName)
                .replace("{itemTime}", itemTime);

        EmailContentBO emailContentBO = new EmailContentBO();
        emailContentBO.setEmailHost(EmailConstants.SMTP_163_HOST);
        emailContentBO.setTransportType(EmailConstants.SMTP_TRANSPORT_TYPE);
        emailContentBO.setFromUser(EmailConstants.PERSONAL_EMAIL_USER);
        emailContentBO.setFromEmail(EmailConstants.PERSONAL_EMAIL_NAME);
        emailContentBO.setAuthCode(EmailConstants.PERSONAL_EMAIL_AUTH_CODE);
        emailContentBO.setToEmail("878478652@qq.com");
        emailContentBO.setSubject("证书办理查询");
        emailContentBO.setEmailContent(emailContent);

        try {
            JavaxJavaMailClient.sendEmail(emailContentBO);
        } catch (MessagingException e) {
            System.out.println("发送证书查询邮件异常，请注意关注！");
            e.printStackTrace();
        }

    }


}
