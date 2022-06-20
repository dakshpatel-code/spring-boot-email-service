package com.example.app.controller;

import com.example.app.bean.EmailBean;
import com.example.app.bean.JavaMailProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest
public class EmailAppControllerTest {

    @Resource
    JavaMailProperties javaMailProperties;
    @Resource
    EmailAppController emailAppController;

    @Test
    public void sendMailTest() {
        EmailBean email = new EmailBean();
        email.setFrom(javaMailProperties.getUsername());
        email.setTo(new String[] {javaMailProperties.getUsername()});
        email.setSubject("test mail");
        email.setCc(new String[] {javaMailProperties.getUsername()});
        email.setBcc(new String[] {javaMailProperties.getUsername()});
        email.setText("test mail text");

        Map<String, Object> map = new HashMap<>();
        map.put("status-code", 200);
        map.put("message", "message sent");

        HashMap<Object, Object> response = emailAppController.sendEmail(email);

        assert response.equals(map);
    }

    @Test
    public void receiveMailTest() {
        Set<EmailBean> messages = emailAppController.receiveMessages(1);
        assert messages.size() > 0;
    }
}
