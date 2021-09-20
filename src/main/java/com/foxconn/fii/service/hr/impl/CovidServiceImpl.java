package com.foxconn.fii.service.hr.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.primary.repository.LineRepository;
import com.foxconn.fii.request.hr.UserCovid;
import com.foxconn.fii.service.hr.CovidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class CovidServiceImpl implements CovidService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LineRepository lineRepository;

    @Override
    public Map<String, UserCovid> getInfoUserFromOppm(TimeSpan timeSpan) {
        Map<String, UserCovid> mUsers = new LinkedHashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("", headers);
//        String url = "https://10.224.81.70:6443/hr-system/api/oppm/detail_user_list?empNo=&startDate="+getStrDate(timeSpan)+"&shift=DAY&factory=FUNING&bu=&action=COMER";
        String url = "https://10.224.81.70:6443/hr-system/api/oppm/detail_user_list?empNo=&startDate="+getStrDate(DataStatic.getTimeInDay())+"&cft=Landis Gyr&shift="+DataStatic.getShiftName()+"&bu=&action=COMER";
//        url = "http://10.224.81.100:8000/api?ip=10.224.56.250&port=26152&time=0.5";
        ResponseEntity<String> responseEntity = null;
        Map<String, Object> dataResponse = new HashMap<>();
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String data = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            dataResponse = objectMapper.readValue(data, new TypeReference<Map<String, Object>>(){});
            String status = (String) dataResponse.get("status");
            if(status.equalsIgnoreCase("OK")){
                List<Map<String, Object>> mListEmp = (List<Map<String, Object>>) dataResponse.get("result");
                if(mListEmp.size() > 0){
                    for(int i = 0; i < mListEmp.size(); i++){
                        UserCovid itemCovid = new UserCovid(mListEmp.get(i));
                        mUsers.put(itemCovid.getEmpId(), itemCovid);
                    }
                }
            }
        } catch (RestClientException e) {
            //  return ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mUsers;
    }

    @Override
    public Map<String, Object> checkUserCovid(TimeSpan timeSpan){
//        TimeSpan timeSpan = DataStatic.getTimeInDay();
        List<Map<String, Object>> mData = lineRepository.jpqlGetDataCovid(timeSpan.getStartDate());
        Map<String, List<Map<String, Object>>> mapData = DataStatic.convertListToMapList(mData, "wo", false);
        Map<String, UserCovid> userHr = getInfoUserFromOppm(timeSpan);
        List<Object> dataUncheck = new ArrayList<>();

        if(mData.size() > 0){
            if(userHr.keySet().size() > 0){
                for(String empId : userHr.keySet()){
                    if(mapData.get(empId) == null){
                        dataUncheck.add(userHr.get(empId));
                        System.out.println(empId+" - Data NULL");
                    }else{
                        System.out.println(empId+" - Data OK");
                    }
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_oppm", userHr.keySet().size());
        result.put("total_paperless", mData.size());
        result.put("total_no_data", dataUncheck.size());
        result.put("data_check", dataUncheck);
        return result;
    }

    private String getStrDate(TimeSpan timeSpan){
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeSpan.getStartDate());
        String year = cal.get(Calendar.YEAR)+"";
        String day = "";
        String month = "";

        if (cal.get(Calendar.DAY_OF_MONTH) < 10) day = "0"+cal.get(Calendar.DAY_OF_MONTH); else day = ""+cal.get(Calendar.DAY_OF_MONTH);
        if(cal.get(Calendar.MONTH)+1 < 10) month = "0"+(cal.get(Calendar.MONTH)+1); else month = ""+(cal.get(Calendar.MONTH)+1);

        return year+month+day;
    }

}
