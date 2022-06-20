package com.example.app.controller;

import com.example.app.bean.EmailBean;
import com.example.app.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("api/1.0")
public class EmailAppController {

    @Value("${spring.mail.properties.mail.from}")
    String emailFrom;
    @Autowired
    EmailService emailService;

    @PostMapping(value = "/send", consumes = "application/json")
    public HashMap<Object, Object> sendEmail(@RequestBody EmailBean emailBean) {
        HashMap<Object, Object> map = new HashMap<>();;
        try {
            log.info("received message : {}", emailBean);
            emailBean.setFrom(emailFrom);
            emailService.send(emailBean);
            log.info("message sent");
            map.put("status-code", 200);
            map.put("message", "message sent");
            return map;
        } catch (MailException | IOException ex) {
            log.error("exception while processing message {}", ex.getMessage());
            ex.printStackTrace();
            map.put("status-code", 500);
            map.put("message", "internal server error");
        }
        return map;
    }

    @GetMapping(value = "/receive/{limit}", produces = "application/json")
    @ResponseBody
    public Set<EmailBean> receiveMessages(@PathVariable int limit) {
        Set<EmailBean> messages = Collections.emptySet();
        try {
            log.info("received message call start");
             messages = emailService.receive(limit);
            log.info("received message call end");
        } catch (MessagingException | IOException | MailException ex){
            log.error("exception while processing message {}",ex.getMessage());
            ex.printStackTrace();
        }
        return messages;
    }
}
