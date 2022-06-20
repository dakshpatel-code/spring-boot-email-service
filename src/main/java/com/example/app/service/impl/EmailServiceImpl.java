package com.example.app.service.impl;

import com.example.app.bean.EmailBean;
import com.example.app.bean.JavaMailProperties;
import com.example.app.service.EmailService;
import com.example.app.util.CsvUtility;
import com.example.app.util.EmailUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @Resource
    JavaMailProperties javaMailProperties;

    @Resource
    CsvUtility csvUtility;

    @Override
    public boolean send(EmailBean message) throws MailException, IOException {
        boolean flag = false;
        try {
            Optional.ofNullable(message.transform()).ifPresent(javaMailSender::send);
            csvUtility.write(message);
            flag = true;
        }catch (MailException | IOException ex) {
            throw ex;
        }
        return flag;
    }

    @Override
    public Set<EmailBean> receive(int limit) throws MessagingException, IOException {
        Set<EmailBean> beans = new LinkedHashSet<>();
        try (Store store = javaMailSender.getSession().getStore(javaMailProperties.getProtocol());){
            if(!store.isConnected()) {
                store.connect(javaMailProperties.getUsername(), javaMailProperties.getPassword());
            }
            try(Folder inboxFolder = store.getFolder("INBOX")) {
                inboxFolder.open(Folder.READ_WRITE);
                Message[] messages = inboxFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (int i = 0; i < limit; i++) {
                    Message message = messages[i];
                    EmailBean bean = new EmailBean();
                    bean.setFrom(String.valueOf(message.getFrom()[0]));
                    bean.setSubject(message.getSubject());
                    bean.setText(EmailUtility.getTextFromMessage(message));
                    Optional.ofNullable(message.getAllRecipients()).ifPresent(rec-> {
                        bean.setCc(Arrays.stream(rec).map(String::valueOf).toArray(String[]::new));
                    });
                    Optional.ofNullable(message.getReplyTo()).ifPresent(rec-> {
                        bean.setTo(Arrays.stream(rec).map(String::valueOf).toArray(String[]::new));
                    });
                    Optional.ofNullable(message.getReceivedDate()).ifPresent(bean::setDate);
                    beans.add(bean);
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            } catch (MessagingException | IOException e) {
                log.error(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        } catch (MessagingException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return beans;
    }
}
