package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.primary.model.agile.AgileEcn;
import com.foxconn.fii.data.primary.repository.AgileEcnRepository;
import com.foxconn.fii.request.agile.Ecn;
import com.foxconn.fii.service.AgileEcnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AgileEcnServiceImpl implements AgileEcnService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AgileEcnRepository agileEcnRepository;

    @Override
    public AgileEcn requestToAgile(String mEcnNo) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        Ecn ecn = new Ecn(mEcnNo);
        String body = ecn.toString();


        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        System.out.println("abccc-----"+entity);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(DataStatic.AGILE.URL_DOWNLOAD_ECN, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### HTTP request : ", e);
            //  return ;
        }
        Map<String, Object> dataResult;
        AgileEcn itemEcn;
        try {

            dataResult = objectMapper.readValue(responseEntity.getBody(),  new TypeReference<Map<String, Object>>(){});
            itemEcn = new AgileEcn(dataResult);
            System.out.println(responseEntity.getBody());
        }catch (Exception e){
            itemEcn = new AgileEcn(mEcnNo, responseEntity.getBody());
        }
        return itemEcn;
    }

    @Override
    public Object checkAndDownloadEcn() {
        List<String> listEcnNo = agileEcnRepository.jpqGetListEcnNoNotSyn();
        List<AgileEcn> result = new ArrayList<>();
        if(listEcnNo.size() > 0){
            for(int i = 0; i < listEcnNo.size(); i++){
                System.out.println(listEcnNo.get(i)+"___________");
                String ecnNo=listEcnNo.get(i);
                if (!ecnNo.contains("ECN")&&!ecnNo.contains("NA")){
                    ecnNo="ECN"+ecnNo;
                }
                AgileEcn item = requestToAgile(ecnNo);
                agileEcnRepository.save(item);
                result.add(item);
            }
        }
        return result;
    }
}
