package com.foxconn.fii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foxconn.fii.request.hr.UserCovid;

import java.io.IOException;
import java.util.List;

public interface MailService {
    void sendMail(String listUser, String title, String content) throws JsonProcessingException;

    void sendMailAndFile(List<UserCovid> mData, String subContent) throws IOException;
}
