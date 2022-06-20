package com.example.app.util;

import org.springframework.util.MimeTypeUtils;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

public class EmailUtility {
    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType(MimeTypeUtils.TEXT_PLAIN_VALUE)) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    public static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType(MimeTypeUtils.TEXT_PLAIN_VALUE)) {
                result = String.format("%sn%s", result, bodyPart.getContent());
                break;
            } else if (bodyPart.isMimeType(MimeTypeUtils.TEXT_HTML_VALUE)) {
                String html = (String) bodyPart.getContent();
                result = String.format("%sn%s", result, org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = String.format("%s%s", result, getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result;
    }
}
