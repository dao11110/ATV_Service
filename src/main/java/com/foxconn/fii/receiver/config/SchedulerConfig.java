package com.foxconn.fii.receiver.config;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.request.hr.UserCovid;
import com.foxconn.fii.service.*;
import com.foxconn.fii.service.hr.CovidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private RFaiSmtConfigService rFaiSmtConfig;

    @Autowired
    private RWoRequestService rWoRequestService;

    @Autowired
    private OutputService outputService;

    @Autowired
    private LockLineService lockLineService;

    @Autowired
    private CovidService covidService;

    @Autowired
    private MailService mailService;

    @Scheduled(fixedDelay = 300000, initialDelay = 300000) //300000
    public void b04CrawMoFaiSmt(){
        rFaiSmtConfig.checkDataWoB04(null, DataStatic.ITSFC.FACTORY.B04);
        rWoRequestService.checkRWoRequestNew(DataStatic.ITSFC.FACTORY.B04);
        rFaiSmtConfig.checkDataWoF12(null, DataStatic.ITSFC.FACTORY.F12);
        rWoRequestService.checkRWoRequestNew(DataStatic.ITSFC.FACTORY.F12);

    }


//    @Scheduled(fixedDelay = 10000, initialDelay = 10000) //300000
    public void c02CheckStatusForWo(){
        lockLineService.lockLineSmtC02("C2","PROCESS");
    }

    @Scheduled(cron = "${batch.cron.sync.output-me-b04}")
    //@Scheduled(cron = "0 19 13 * * *")
    public void getOutput(){
        outputService.getModel();
    }

    @Scheduled(cron = "${batch.cron.sync.hr-covid-b04}")
    public void sendMailNotifyB04() throws IOException {
        Map<String, Object> mData = covidService.checkUserCovid(DataStatic.getTimeInDay());
        List<UserCovid> mUser = (List<UserCovid>) mData.get("data_check");
        if(mUser.size() > 0){
            Integer oppm = (Integer) mData.get("total_oppm");
            Integer noData = (Integer) mData.get("total_no_data");
            String subContent = "<b>Statistic</b><br>" +
                    "- Total Employee: "+oppm.intValue()+" <br>" +
                    "- Have temperature in system: "+(oppm.intValue()-noData.intValue())+" <br>" +
                    "<span style='color:#CA5100'>- Don't Have temperature in system: "+noData.intValue()+"</span>";
            mailService.sendMailAndFile(mUser,subContent);
        }
    }
}
