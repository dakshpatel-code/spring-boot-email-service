package com.example.app.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("spring.mail.properties.mail")
@Component
public class JavaMailProperties {
    String protocol;
    String host;
    String username;
    String password;
    String folder;
}
