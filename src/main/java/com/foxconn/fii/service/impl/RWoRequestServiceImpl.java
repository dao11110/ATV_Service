package com.foxconn.fii.service.impl;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import com.foxconn.fii.data.b04sfc.repository.B04RWoRequestRepository;
import com.foxconn.fii.data.primary.model.RWoRequest;
import com.foxconn.fii.data.primary.repository.RWoRequestRepository;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.RWoRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RWoRequestServiceImpl implements RWoRequestService {

    @Autowired
    @Qualifier(value = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private B04RWoRequestRepository b04RWoRequestRepository;

    @Autowired
    private RWoRequestRepository rWoRequestRepository;

    @Override
    public Response checkPnByWo(String wo) {
        List<Date> mTimes = rWoRequestRepository.jpqlCheckTimeDownloadBomByWo(wo);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2001, 0, 1);
        Date timeCheck = calendar.getTime();
        if(mTimes.size() > 0){
            timeCheck = mTimes.get(0);
        }

        List<B04RWoRequest> b04Data = b04RWoRequestRepository.jpqlGetPnsByWo(wo, timeCheck);
        if(b04Data.size() > 0){
            for(int i = 0; i < b04Data.size(); i++){
                RWoRequest item = new RWoRequest(b04Data.get(i));
                rWoRequestRepository.save(item);
            }
        }

        return new Response(DataStatic.Status.SUCCESS, "Load data success", b04Data, b04Data.size());
    }
}
