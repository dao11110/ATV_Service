package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import com.foxconn.fii.data.primary.model.agile.AgileBom;
import com.foxconn.fii.data.primary.model.agile.AgileBomPn;
import com.foxconn.fii.data.primary.model.agile.AgileBomPnMfr;
import com.foxconn.fii.data.primary.repository.AgileBomPnRepository;
import com.foxconn.fii.data.primary.repository.AgileBomRepository;
import com.foxconn.fii.data.primary.repository.BomPnMfrRepository;
import com.foxconn.fii.request.agile.Bom;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.AgileBomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class AgileBomServiceImpl implements AgileBomService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AgileBomPnRepository agileBomPnRepository;

    @Autowired
    private BomPnMfrRepository bomPnMfrRepository;

    @Autowired
    private AgileBomRepository agileBomRepository;

    @Override
    public List<Map<String, Object>> requestToAgile(String modelName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        Bom bom = new Bom(modelName);
        String body = bom.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(DataStatic.AGILE.URL_DOWNLOAD_BOM, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### HTTP request : ", e);
            //  return ;
        }
        List<Map<String, Object>> dataResult = new ArrayList<>();
        try {

            dataResult = objectMapper.readValue(responseEntity.getBody(),  new TypeReference<List<Map<String, Object>>>(){});
            sycnDataAgile(dataResult, modelName);
        }catch (Exception e){
            AgileBom itemAgileBom = new AgileBom();
            itemAgileBom.setModelName(modelName);
            itemAgileBom.setType("WEIBAO");
            agileBomRepository.save(itemAgileBom);
            return new ArrayList<>();
        }

        return dataResult;
    }


    @Override
    public Response sycnDataAgile(List<Map<String, Object>> mData, String mModelName) {
        List<AgileBomPn> result = new ArrayList<>();
        if(mData.size() > 0){
            for(int i = 0; i < mData.size(); i++){
                AgileBomPn itemPn = new AgileBomPn(mData.get(i));
                itemPn.setProduct(mModelName);
                agileBomPnRepository.save(itemPn);
                List<Map<String, Object>> amls = (List<Map<String, Object>>) mData.get(i).get("AML");
                if(amls.size() > 0){
                    for(int j = 0; j < amls.size(); j++){
                        AgileBomPnMfr itemMfr = new AgileBomPnMfr(amls.get(j));
                        itemMfr.setIdBomPn(itemPn.getId());
                        bomPnMfrRepository.save(itemMfr);
                    }
                }
                result.add(itemPn);
            }
        }
        return  new Response(1, "Load data successful", result, result.size());
    }

    @Override
    public Object downloadBoms() {
        List<String> mModel = agileBomPnRepository.jpqlGetListModel();
        if(mModel.size() > 0){
            for(int i = 0; i < mModel.size(); i++){
                requestToAgile(mModel.get(i));
            }
        }
        return mModel;
    }

    @Override
    public Object checkVersionBomAgileAndSap(List<B04RWoRequest> mListPn) {
        if(mListPn.size() > 0){
            List<String> listModels = new ArrayList<>();
            for(int i = 0; i < mListPn.size(); i++){
                if(listModels.indexOf(mListPn.get(i).getPNo()) == -1){
                    listModels.add(mListPn.get(i).getPNo());
                }
            }

            if(listModels.size() > 0){

            }
        }

        return null;
    }
}
