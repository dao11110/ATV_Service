package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.b04sfc.repository.RSmtFaiRepository;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.data.primary.repository.RSmtFaiConfigRepository;
import com.foxconn.fii.request.b04sfc.WO;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.B04Service;
import com.foxconn.fii.service.RFaiSmtConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class B04ServiceImpl implements B04Service {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Map<String, Object>> getMaterialSolderByWo(RSmtFaiConfig mData) {
        WO param = new WO(mData);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();

        String body = param.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String url = DataStatic.ITSFC.B04.GET_SOLDER;
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### HTTP request : ", e);
            //  return ;
        }
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        try {
            map = objectMapper.readValue(responseEntity.getBody(),  new TypeReference<Map<String, List<Map<String, Object>>>>(){});
            if(map.get("data") == null){
                return new ArrayList<>();
            }
            return map.get("data");
        }catch (Exception e){
            log.error(String.valueOf(e));
            return new ArrayList<>();
        }
//        return null;
    }

    @Override
    public List<Map<String, Object>> getMaterialSolderByWo(WO mData) {
        WO param = mData;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();

        String body = param.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String url = DataStatic.ITSFC.B04.GET_SOLDER;
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### HTTP request : ", e);
            //  return ;
        }
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        try {
            map = objectMapper.readValue(responseEntity.getBody(),  new TypeReference<Map<String, List<Map<String, Object>>>>(){});
            if(map.get("data") == null){
                return new ArrayList<>();
            }
            return map.get("data");
        }catch (Exception e){
            log.error(String.valueOf(e));
            return new ArrayList<>();
        }
    }
}
