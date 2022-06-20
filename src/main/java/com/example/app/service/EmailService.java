package com.example.app.service;

import com.example.app.bean.EmailBean;
import org.springframework.mail.MailException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Set;

public interface EmailService {

    boolean send(EmailBean emailBeanConsumer) throws MailException, IOException;
    Set<EmailBean> receive(int limit) throws MessagingException, IOException;
}
