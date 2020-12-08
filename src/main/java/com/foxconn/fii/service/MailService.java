package com.foxconn.fii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Bean;

public interface MailService {

    @Bean("sendMail")
    void sendMail(String listUser, String title, String content) throws JsonProcessingException;
}
