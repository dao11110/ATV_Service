package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.data.primary.repository.MaterialRepository;
import com.foxconn.fii.request.b04sfc.WO;
import com.foxconn.fii.service.AllpartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class AllpartServiceImpl implements AllpartService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public List<Map<String, Object>> getMaterialSolderByWo(RSmtFaiConfig mData, String mFactory) {
        WO param = new WO(mData, mFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();

        String body = param.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String url = DataStatic.ITSFC.mapIpServerByFactory(mFactory)+DataStatic.ITSFC.FUNCTION.GET_SOLDER;
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
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> dataRoSHByWO(RSmtFaiConfig mItem, String mFactory){
        List<Map<String, Object>> mSolder = new ArrayList<>();
        mSolder.addAll(getMaterialSolderByWo(mItem, mFactory));
        String strSolder = "";
        String dataFill = "";
        if(mSolder.size() > 0){
            for(int i = 0; i < mSolder.size(); i++){
                strSolder += (String) mSolder.get(i).get("KP_NO") + ", ";
                List<Map<String, Object>> mType = materialRepository.jpqlGetListMaterialRoHS((String) mSolder.get(i).get("KP_NO"));
                if(mType.size() > 0){
                    dataFill = "Non-ROHS[有鉛 Có chì]";
                }else{
                    dataFill = "ROHS[無鉛 Không chì]";
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("materials", strSolder);
        result.put("material_fill",dataFill);
        return result;
    }

    @Override
    public String dataEcnNo(RSmtFaiConfig mItem, String mFactory){
        List<Map<String, Object>> mEcn = new ArrayList<>();
        mEcn.addAll(getEcnNoByWo(mItem, mFactory));
        String result = null;
        if(mEcn.size() > 0){
            result = (String) mEcn.get(0).get("ECN_NO");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getEcnNoByWo(RSmtFaiConfig mData, String mFactory) {
        WO param = new WO(mData, mFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();

        String body = param.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String url = DataStatic.ITSFC.mapIpServerByFactory(mFactory)+DataStatic.ITSFC.FUNCTION.GET_ECN_NO;

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
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getMachineByWo(WO mData, String mFactory) {
        WO param = mData;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        ObjectMapper objectMapper = new ObjectMapper();

        String body = param.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String url = DataStatic.ITSFC.mapIpServerByFactory(mFactory)+DataStatic.ITSFC.FUNCTION.GET_MACHINE;
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### HTTP request : ", e);
            //  return ;
        }
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        try {
            System.out.println("Data: "+responseEntity.getBody());

            map = objectMapper.readValue(responseEntity.getBody(),  new TypeReference<Map<String, List<Map<String, Object>>>>(){});

            if(map.get("data") == null){
                return new ArrayList<>();
            }

            return map.get("data");
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
