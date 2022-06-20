package com.example.app.service;

import com.example.app.bean.EmailBean;
import com.example.app.bean.JavaMailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class EmailAppServiceTest {

    @Resource
    JavaMailProperties javaMailProperties;
    @Resource
    JavaMailSenderImpl javaMailSender;

    @Test
    public void testMail() {
        EmailBean email = new EmailBean();
        email.setFrom(javaMailProperties.getUsername());
        email.setTo(new String[]{javaMailProperties.getUsername()});
        email.setSubject("test subject");
        email.setText("test message");
        javaMailSender.send(email.transform());
        try (Store store = javaMailSender.getSession().getStore(javaMailProperties.getProtocol());){
            if(!store.isConnected()) {
                store.connect(javaMailProperties.getUsername(), javaMailProperties.getPassword());
            }
            try(Folder inboxFolder = store.getFolder("INBOX")) {
                inboxFolder.open(Folder.READ_WRITE);
                Message[] messages = inboxFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                Set<EmailBean> beans = new LinkedHashSet<>();
                for (int i = 0; i < 1; i++) {
                    Message message = messages[i];
                    EmailBean bean = new EmailBean();
                    bean.setFrom(String.valueOf(message.getFrom()[0]));
                    bean.setSubject(message.getSubject());
                    bean.setText(getTextFromMessage(message));
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
                assert beans.size() > 0;
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }
}
