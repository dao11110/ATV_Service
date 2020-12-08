package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Override
    public void sendMail(String listUser, String title, String content) throws JsonProcessingException {
        listUser = "cpe-vn-fii-app@mail.foxconn.com";
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("title",title);

        tmpMap.put("body", "<img src=\"http://10.224.81.94:8888/paperless/ws-data/images/IMG20201016094826.jpg\" width=\"30px\" height=\"30px\">");

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("system", "MAIL");
        bodyMap.put("type", "TEXT");
        bodyMap.put("source", "Paperless");
        bodyMap.put("from", "");
        bodyMap.put("toUser", listUser);
        bodyMap.put("toGroup", null);
        bodyMap.put("message", objectMapper.writeValueAsString(tmpMap));
        String body = objectMapper.writeValueAsString(bodyMap);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyMap, headers);

        ResponseEntity<String> responseEntity;
        try {
            RestTemplate restTemplate = new RestTemplate();
            responseEntity = restTemplate.exchange("http://10.224.24.33:8888/notify-service/api/notify", HttpMethod.POST, entity, String.class);
            System.out.println("Send mail: "+responseEntity.getStatusCode().toString());
        } catch (RestClientException e) {
            System.out.println("Error: "+e.toString());
            //return false;
        }

    }
}
