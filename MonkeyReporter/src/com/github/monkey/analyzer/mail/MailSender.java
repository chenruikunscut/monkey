package com.github.monkey.analyzer.mail;

/**
 * Created by DannyChen on 2016/1/5.
 */
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by DannyChen on 2016/1/5.
 */
public class MailSender {
    //定义邮件设置的properties
    private final Properties properties = new Properties();
    //登录邮箱验证
    private MailAuthenticator authenticator;
    //邮箱session
    private Session session;

    public MailSender(final String smtpHost,final String username, final String password) {
        init(username,password,smtpHost);
    }


    //初始化邮箱
    private void init(String username, String password, String smtpHost){
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.host",smtpHost);
        properties.put("mail.smtp.port","25");
        //验证邮箱登录
        authenticator = new MailAuthenticator(username,password);
        //创建session
        session = Session.getInstance(properties,authenticator);
    }
    public void sendMail(String recipient, String subJect, Object content){
        final MimeMessage message = new MimeMessage(session);
        try {
            //设置发件人
            message.setFrom(new InternetAddress("chenruikun@jd.com"));
            //设置收件人
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
            //设置主题
            message.setSubject(subJect);
            //设置邮件内容
            message.setContent(content.toString(),"text/html;charset=utf-8");
            //发送
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

