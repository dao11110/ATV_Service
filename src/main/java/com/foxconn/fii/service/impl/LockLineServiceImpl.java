package com.foxconn.fii.service.impl;

import com.foxconn.fii.data.b04sfc.model.B04RSmtFai;
import com.foxconn.fii.data.b04sfc.repository.B04RSmtFaiRepository;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.data.primary.repository.RSmtFaiConfigRepository;
import com.foxconn.fii.service.LockLineService;
import com.foxconn.fii.service.MailService;
import com.foxconn.fii.service.PaperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LockLineServiceImpl implements LockLineService {

    @Autowired
    private RSmtFaiConfigRepository rSmtFaiConfigRepository;

    @Autowired
    private B04RSmtFaiRepository b04RSmtFaiRepository;

    @Autowired
    private PaperLogService paperLogService;

    @Autowired
    private MailService mailService;

    @Override
    public Object lockLineSmtC02(String build, String status) {
        List<RSmtFaiConfig> mFai = rSmtFaiConfigRepository.jpqlCheckDataStatusByTime(build, status, 240);
        if(mFai.size() > 0){
            String mLog = "";
            for(int i = 0; i < mFai.size(); i++){
                List<B04RSmtFai> itemB4 = b04RSmtFaiRepository.jpqlGetDataSmtFai(mFai.get(i).getWo(), mFai.get(i).getStation());
                if(itemB4.size() > 0){
                    mLog += "Data FAIL: "+mFai.get(i).getId();
                    itemB4.get(0).setStatus("FAIL");
                    mFai.get(i).setStatus("FAIL");
                    rSmtFaiConfigRepository.save(mFai.get(i));
                    b04RSmtFaiRepository.save(itemB4.get(0));
                }
            }
            if(mLog.trim().length() > 0){

            }
            paperLogService.addLog("Paperless(C02-Allpart-SMT)", mLog, "AUTO UPDATE DATA", "C02", "QA");
        }
        return false;
    }
}
