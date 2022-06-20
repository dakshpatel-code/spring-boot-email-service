package com.example.app.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"from", "to", "cc", "bcc", "subject", "text", "date"})
public class EmailBean{
    private String from;
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String text;
    private Date date;

    public SimpleMailMessage transform(){
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.from);
        message.setTo(this.to);
        message.setCc(this.cc);
        message.setBcc(this.bcc);
        message.setSubject(this.subject);
        message.setText(this.text);
        message.setSentDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        return message;
    }
}
