package com.foxconn.fii.receiver.config;

import com.foxconn.fii.service.OutputService;
import com.foxconn.fii.service.RFaiSmtConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private RFaiSmtConfigService rFaiSmtConfig;

    @Autowired
    private OutputService outputService;

    @Scheduled(fixedDelay = 300000, initialDelay = 300000) //300000
    public void b04CrawMoFaiSmt(){
        rFaiSmtConfig.checkNewDataWo(null);
    }

    @Scheduled(cron = "${batch.cron.sync.output-me-b04}")
    public void getOutput(){
        outputService.getModel();
    }
}
