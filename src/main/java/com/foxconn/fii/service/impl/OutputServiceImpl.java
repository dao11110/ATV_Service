package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.data.primary.model.Line;
import com.foxconn.fii.data.primary.model.Model;
import com.foxconn.fii.data.primary.model.Output;
import com.foxconn.fii.data.primary.repository.LineRepository;
import com.foxconn.fii.data.primary.repository.ModelRepository;
import com.foxconn.fii.data.primary.repository.OutputRepository;
import com.foxconn.fii.service.OutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class OutputServiceImpl implements OutputService {

    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private OutputRepository outputRepository;

    public void insertOutput(String time, Integer line_id, Integer model_id, Integer output_value){
        Output output = new Output();
        output.setDateTime(time);
        output.setIdLine(line_id);
        output.setIdModel(model_id);
        output.setOutputValue(output_value);
        outputRepository.save(output);
    }

    public void getModel()
    {
        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer monthS = calendar.get(Calendar.MONTH) + 1;
        Integer dayS = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.DATE, -1);
        Integer yearE = calendarEnd.get(Calendar.YEAR);
        Integer monthE = calendarEnd.get(Calendar.MONTH) + 1;
        Integer dayE = calendarEnd.get(Calendar.DAY_OF_MONTH);

        String sDate = year+"/"+monthS+"/"+dayS+" 07:30:00";
        String eDate = yearE+"/"+monthE+"/"+dayE+" 07:30:00";
        System.out.println("TIME SPAN: "+ eDate + " - " + sDate);

        //API getModel
        String uri = "http://10.224.81.70:8888/api/test/sfc/model?factory=b04&"+
                "time_span= " + eDate + " - " + sDate;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.set("factory", "b04");
//        headers.set("time_span", "01/01/2020 07:30:00 - 01/01/2021 07:30:00");
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        List<String> dataRequest = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataRequest.addAll(objectMapper.readValue(response.getBody(), new TypeReference<List<String>>(){}));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dataRequest.size());

        for (String s : dataRequest) {
            System.out.println(s);
            int model_id;
            List<Model> models = modelRepository.jpqlGetModelByName(s);
            if (models.size() == 0) {
                Model newModel = new Model();
                newModel.setName(s);
                newModel.setIdBu(16);
                newModel.setIdTeam(4);
                modelRepository.save(newModel);
                model_id =  newModel.getId();
            } else {
                model_id =  models.get(0).getId();
                System.out.println("MODEL: Du lieu model:  "+ s + " da ton tai!");
            }

            //API GET OUTPUT
            String urlAPI_output = "http://10.224.81.70:8888/api/test/group/output?"
                    +"factory=b04&modelName=" + s +"&timeSpan=" + eDate + " - " + sDate;

            RestTemplate restTemplate_output = new RestTemplate();
            HttpHeaders headers_output = new HttpHeaders();
            headers_output.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity_output = new HttpEntity<String>(headers);
            ResponseEntity<String> response_output = restTemplate_output.exchange(urlAPI_output, HttpMethod.GET, entity_output, String.class);
            Map<String, Object> mOutput = new HashMap<>();
            try {
                mOutput = objectMapper.readValue(response_output.getBody(),  new TypeReference<Map<String, Object>>(){});
            }catch (Exception e){
                log.info(String.valueOf(e));
            }

            for(String key : mOutput.keySet()){
                Map<String, Object> itemDay = (Map<String, Object>) mOutput.get(key);
                for(String group : itemDay.keySet()){
                    String line_group = null;
                    Map<String, Object> itemLine = (Map<String, Object>) itemDay.get(group);
                    for(String line : itemLine.keySet()){
                        Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) itemLine.get(line) ;
                        try {
                            if(line.equals("TOTAL")==false){
                                if(group.equals("")){
                                    line_group = "B04-ME-Main-PTH-" + line.replace("L", "");
                                }else if(group.equals("SMT")){
                                    line_group = "B04-ME-Main-SMT-" + line.replace("C", "");
                                }
                                Integer day, night;
                                try {
                                    day =  (Integer) data.get("DAY").get("output");
                                }catch (Exception e){
                                    day = 0;
                                }
                                try {
                                    night =  (Integer) data.get("NIGHT").get("output");
                                }catch (Exception e){
                                    night = 0;
                                }

                                Integer output = day + night;
                                String time = year+ "/"+ key;
                                List<Line> lineSQL = lineRepository.jpqlGetLineByNameLine(line_group);

                                if(lineSQL.size() > 0){
                                    insertOutput(time, lineSQL.get(0).getId(), model_id, output);
                                }
                            }else {
                            }
                        }catch (Exception e){
                        }
                    }
                }
            }
        }
    }
}
