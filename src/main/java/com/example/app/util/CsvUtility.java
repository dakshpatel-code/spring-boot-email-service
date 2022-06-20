package com.example.app.util;

import com.example.app.bean.EmailBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
@Component
public class CsvUtility {

    @Value("src/main/resources/data/output.csv")
    File file;

    String[] nameMapping;
    @PostConstruct
    void initFile() throws IOException {
        Writer writer = new FileWriter(file, true);
        CsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"from", "email to", "email cc", "email bcc", "email subject", "email text", "email date"};
        nameMapping = new String[] {"from", "to", "cc", "bcc", "subject", "text", "date"};
        csvWriter.writeHeader(csvHeader);
    }
    public void write(EmailBean emailBeans) throws IOException {
        try(Writer writer = new FileWriter(file, true);
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer,CsvPreference.STANDARD_PREFERENCE)) {
            csvWriter.write(emailBeans, nameMapping);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @PreDestroy
    void removeFile() throws IOException {
        if(file.exists()){
            file.delete();
        }
    }
}
