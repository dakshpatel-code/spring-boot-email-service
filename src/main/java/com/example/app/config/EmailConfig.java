package com.example.app.config;

import com.example.app.bean.JavaMailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Session;
import java.util.Properties;

@Configuration
public class EmailConfig {
    @Autowired
    JavaMailProperties javaMailProperties;
    @Bean
    public Session getSession() {
        Properties properties = new Properties();

        properties.put("mail.imap.host", javaMailProperties.getHost());
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.trust", javaMailProperties.getHost());

        return Session.getDefaultInstance(properties);
    }
}
