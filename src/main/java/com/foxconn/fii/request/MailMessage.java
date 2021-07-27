package com.foxconn.fii.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MailMessage {

    private String title;

    private String body;

    private String attach;

    private String fileName;

    private List<String> cc = new ArrayList<>();

    public static MailMessage of (String title, String body) {
        return of(title, body, "", "");
    }

    public static MailMessage of (String title, String body, String attach, String fileName) {
        MailMessage result = new MailMessage();
        result.setTitle(title);
        result.setBody(body);
        result.setAttach(attach);
        result.setFileName(fileName);
        return result;
    }

    public static MailMessage of (String title, String body, List<String> cc) {
        MailMessage result = new MailMessage();
        result.setTitle(title);
        result.setBody(body);
        result.setAttach("");
        result.setFileName("");
        result.setCc(cc);
        return result;
    }
}
