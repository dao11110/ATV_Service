package com.foxconn.fii.service.impl;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.primary.model.RSmtFaiStation;
import com.foxconn.fii.data.primary.repository.RSmtFaiConfigRepository;
import com.foxconn.fii.data.primary.repository.RSmtFaiStationRepository;
import com.foxconn.fii.request.b04sfc.WO;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.AllpartService;
import com.foxconn.fii.service.RFaiSmtStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RFaiSmtStationServiceImpl implements RFaiSmtStationService {

    @Autowired
    private RSmtFaiConfigRepository rSmtFaiConfigRepository;

    @Autowired
    private AllpartService allpartService;

    @Autowired
    private RSmtFaiStationRepository rSmtFaiStationRepository;

    @Override
    public Response getStationInformationByWo(String mFactory, String mWo, Integer idConfig, String lineName) {
        String dev = null;
        WO wo = new WO(mWo, mFactory);
        List<Map<String, Object>> mData = allpartService.getMachineByWo(wo, mFactory);
        Map<String, Map<String, Object>> mMachine = new HashMap<>();
        if(mData.size() > 0){
            for(int i = 0; i < mData.size(); i++){
                String station = (String) mData.get(i).get("STATION");
                String ecnNo = (String) mData.get(i).get("ECN_NO");
                if(mMachine.get(station) == null){
                    String programName = (String) mData.get(i).get("PROGRAM_NAME");
                    String strWo = (String) mData.get(i).get("WO");
                    dev = (String) mData.get(i).get("DEV");

                    Map<String, Object> itemMachine = new HashMap<>();
                    itemMachine.put("STATION", station);
                    itemMachine.put("PROGRAM_NAME", programName);
                    itemMachine.put("WO", strWo);
                    itemMachine.put("ECN_NO", ecnNo+", ");
                    itemMachine.put("DEV", dev);
                    mMachine.put(station, itemMachine);
                }else{
                    String tmpEcn = (String) mMachine.get(station).get("ECN_NO");
                    tmpEcn += ecnNo+", ";
                    mMachine.get(station).put("ECN_NO", tmpEcn);
                }
            }

            if(dev != null){
                rSmtFaiConfigRepository.jpqlUpdateDevById(idConfig, dev);
            }

            List<RSmtFaiStation> dataStation = new ArrayList<>();
            List<Integer> tmpIdStations = new ArrayList<>();
            for(String keyStation : mMachine.keySet()){
                String strStation = (String) mMachine.get(keyStation).get("STATION");
                if(strStation.compareTo(lineName) > 0) {
                    RSmtFaiStation station = new RSmtFaiStation(idConfig, lineName, mMachine.get(keyStation));
                    rSmtFaiStationRepository.save(station);
                    dataStation.add(station);
                    tmpIdStations.add(station.getId());
                }
            }
            if(tmpIdStations.size() > 0){
                rSmtFaiStationRepository.jpqlUpdateIdQcMapByIds(tmpIdStations);
            }

            return new Response(DataStatic.Status.SUCCESS, "Load data success", dataStation, dataStation.size());
        }else{
            return new Response(DataStatic.Status.SUCCESS, "No data", new ArrayList<>(), 0);
        }
    }
}
