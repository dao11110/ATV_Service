package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.b04sfc.repository.RSmtFaiRepository;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.data.primary.repository.MaterialRepository;
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

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RFaiSmtConfigServiceImpl implements RFaiSmtConfigService {

    @Autowired
    @Qualifier(value = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RSmtFaiRepository rSmtFaiRepository;

    @Autowired
    private B04Service b04Service;

    @Autowired
    private RSmtFaiConfigRepository rSmtFaiConfigRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public Response checkNewDataWo(TimeSpan timeSpan) {
        List<Map<String, Object>> maxTimes = rSmtFaiConfigRepository.getMaxTime();
        Date timeMax = null;
        if(maxTimes.size() > 0){
            timeMax = (Date) maxTimes.get(0).get("max_time");
        }else{
            return null;
        }
        List<Map<String, Object>> mData = rSmtFaiRepository.findDataMoByTimeMax(timeMax);
        if(mData.size() > 0){
            List<Integer> idInsert = new ArrayList<>();
            for(int i = 0; i < mData.size(); i++){
                RSmtFaiConfig item = new RSmtFaiConfig(mData.get(i));
                item.setMaterial(dataRoSHByWO(item));
                rSmtFaiConfigRepository.save(item);
                idInsert.add(item.getId());
            }
            rSmtFaiConfigRepository.jpqlUpdateIdQcBeforeInsertByIds(idInsert);
        }
        return new Response(DataStatic.Status.SUCCESS, "Load data success", mData, mData.size());
    }

    @Override
    public Object checkDataTest() {
        List<String> mWo = rSmtFaiConfigRepository.jpqlGetListWo();
        if(mWo.size() > 0){
            for(int i = 0; i < mWo.size(); i++){
                WO itemWo = new WO(mWo.get(i));
                Map<String, Object> itemMaterial = dataRoSHByWO(itemWo);
                String materials = (String) itemMaterial.get("materials");
                String materialFill = (String) itemMaterial.get("material_fill");
//                System.out.println("Test - WO: "+itemWo.getWo()+" - M1: "+materials+" - M2: "+materialFill);
                rSmtFaiConfigRepository.jpqlUpdateDataSolder(mWo.get(i), materials, materialFill);
            }
        }else{

        }
        return 1;
    }

    private Map<String, Object> dataRoSHByWO(RSmtFaiConfig mItem){
        List<Map<String, Object>> mSolder = new ArrayList<>();
        mSolder.addAll(b04Service.getMaterialSolderByWo(mItem));
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

    private Map<String, Object> dataRoSHByWO(WO mItem){
        List<Map<String, Object>> mSolder = new ArrayList<>();
        mSolder.addAll(b04Service.getMaterialSolderByWo(mItem));
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
    public List<Map<String, Object>> getListMedia(TimeSpan timeSpan) {
        List<Map<String, Object>> tmpData = new ArrayList<>();
        List<Map<String, Object>> tmpMedia = rSmtFaiConfigRepository.jpqlGetListMedia(timeSpan.getStartDate(), timeSpan.getEndDate());

        List<String> urls = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();


        for(int i = 0; i < tmpMedia.size(); i++){
            try {
                urls.addAll(objectMapper.readValue((String) tmpMedia.get(i).get("media"), new TypeReference<List<String>>(){}));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(urls.size() > 0){
                for(int j = 0; j < urls.size(); j++){
                    Map<String, Object> item = new HashMap<>();
                    item.put("time", (Date)tmpMedia.get(i).get("create_at"));
                    item.put("url", urls.get(j));
                    tmpData.add(item);
                }
            }
            urls.clear();

        }
        return tmpData;
    }

    @Override
    public Object testFunc() {
        RSmtFaiConfig mData = rSmtFaiConfigRepository.findById(1001).get();
        return b04Service.getMaterialSolderByWo(mData);
    }
}
