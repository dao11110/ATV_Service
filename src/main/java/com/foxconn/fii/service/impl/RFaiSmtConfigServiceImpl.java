package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.b04sfc.model.B04RSmtFai;
import com.foxconn.fii.data.b04sfc.repository.B04RSmtFaiRepository;
import com.foxconn.fii.data.f12sfc.repository.F12RSmtFaiRepository;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.data.primary.repository.MaterialRepository;
import com.foxconn.fii.data.primary.repository.RSmtFaiConfigRepository;
import com.foxconn.fii.data.primary.repository.RSmtFaiStationRepository;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RFaiSmtConfigServiceImpl implements RFaiSmtConfigService {

    @Autowired
    @Qualifier(value = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RSmtFaiStationRepository rSmtFaiStationRepository;

    @Autowired
    private RWoRequestService rWoRequestService;

    @Autowired
    private B04RSmtFaiRepository b04RSmtFaiRepository;

    @Autowired
    private F12RSmtFaiRepository f12RSmtFaiRepository;

    @Autowired
    private AllpartService allpartService;

    @Autowired
    private RSmtFaiConfigRepository rSmtFaiConfigRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private RFaiSmtStationService rFaiSmtStationService;

    @Autowired
    private PaperLogService paperLogService;

    @Override
    public Response checkDataWoB04(TimeSpan timeSpan, String mFactory) {
        List<String> mBuild = new ArrayList<>();
        mBuild.add(DataStatic.ITSFC.FACTORY.B04);
        mBuild.add(DataStatic.ITSFC.FACTORY.C02);
        List<Map<String, Object>> maxTimes = rSmtFaiConfigRepository.getMaxTimeByBuild(mBuild);
        Date timeMax = null;
        if(maxTimes.size() > 0){
            timeMax = (Date) maxTimes.get(0).get("max_time");
        }else{
            return null;
        }
        List<Map<String, Object>> mData = b04RSmtFaiRepository.findDataMoByTimeMax(timeMax);
        if(mData.size() > 0){
            List<Integer> idInsert = new ArrayList<>();
            for(int i = 0; i < mData.size(); i++){
                RSmtFaiConfig item = new RSmtFaiConfig(mData.get(i));
                item.setMaterial(allpartService.dataRoSHByWO(item, mFactory));
                item.setEcnNo(allpartService.dataEcnNo(item, mFactory));
                item.setStatus("PROCESS");
                if(item.getStation().compareTo(DataStatic.ITSFC.FACTORY.B04) >= 0){
                    item.setBuild(DataStatic.ITSFC.FACTORY.B04);
                }else if (item.getStation().compareTo(DataStatic.ITSFC.FACTORY.C02) >= 0){
                    item.setBuild(DataStatic.ITSFC.FACTORY.C02);
                }
                rSmtFaiConfigRepository.save(item);
                idInsert.add(item.getId());
                rFaiSmtStationService.getStationInformationByWo(mFactory, item.getWo(), item.getId(), item.getStation());
            }
            rSmtFaiConfigRepository.jpqlUpdateIdQcBeforeInsertByIds(idInsert);
        }
        return new Response(DataStatic.Status.SUCCESS, "Load data success", mData, mData.size());
    }

    @Override
    public Response checkDataWoF12(TimeSpan timeSpan, String mFactory) {
        List<String> mBuild = new ArrayList<>();
        mBuild.add(DataStatic.ITSFC.FACTORY.F12);
        List<Map<String, Object>> maxTimes = rSmtFaiConfigRepository.getMaxTimeByBuild(mBuild);
        Date timeMax = null;
        if(maxTimes.size() > 0){
            timeMax = (Date) maxTimes.get(0).get("max_time");
        }else{
            return null;
        }
        List<Map<String, Object>> mData = f12RSmtFaiRepository.findDataMoByTimeMax(timeMax);
        if(mData.size() > 0){
            List<Integer> idInsert = new ArrayList<>();
            for(int i = 0; i < mData.size(); i++){
                RSmtFaiConfig item = new RSmtFaiConfig(mData.get(i));
                item.setMaterial(allpartService.dataRoSHByWO(item, DataStatic.ITSFC.FACTORY.F12));
                item.setEcnNo(allpartService.dataEcnNo(item, DataStatic.ITSFC.FACTORY.F12));
                item.setStatus("PROCESS");
                item.setBuild(DataStatic.ITSFC.FACTORY.F12);
                rSmtFaiConfigRepository.save(item);
                idInsert.add(item.getId());
                rFaiSmtStationService.getStationInformationByWo(mFactory, item.getWo(), item.getId(), item.getStation());
            }
            rSmtFaiConfigRepository.jpqlUpdateIdQcBeforeInsertByIds(idInsert);
        }
        return new Response(DataStatic.Status.SUCCESS, "Load data success", mData, mData.size());
    }

    @Override
    public boolean updateStatusFai(String station, String wo) {
        wo="%"+wo+"%";
        List<Map<String, Object>> mData = b04RSmtFaiRepository.jpqlFindMaxRowByStationAndWo(station, wo);
        if(mData.size() > 0){
            Date requestTime = (Date) mData.get(0).get("REQUEST_TIME");
            List<B04RSmtFai> itemB4 = b04RSmtFaiRepository.jpqlGetDataSmtFai(wo, station,requestTime);
            List<RSmtFaiConfig> itemDef = rSmtFaiConfigRepository.jpqlGetDataByWoStationTime(wo, station, requestTime);
            if(itemB4.size() > 0) {
                itemB4.get(itemB4.size()-1).setStatus("PASS");
                b04RSmtFaiRepository.save(itemB4.get(itemB4.size()-1));
            }
            if(itemDef.size() > 0){
                itemDef.get(itemDef.size()-1).setStatus("PASS");
                rSmtFaiConfigRepository.save(itemDef.get(itemDef.size()-1));
            }
            return true;
        }else {
            return false;
        }
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
    public Object test(String wo) {
        return null;
    }
}
