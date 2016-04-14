package com.github.monkey.analyzer.mail;

/**
 * Created by DannyChen on 2016/1/5.
 */
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by DannyChen on 2016/1/5.
 * 邮箱登录校验
 */
public class MailAuthenticator extends Authenticator {
    private String username;
    private String password;

    public MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username,password);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
