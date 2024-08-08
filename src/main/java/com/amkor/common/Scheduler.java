package com.amkor.common;


import com.amkor.service.ATVThanhService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    @Autowired
    private ATVThanhService thanhService;

    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void sendMailAlertFGNotScheduledIn30Days() {
        try {
            log.info("start sending email to alert fg...");
            List<String> listFG = thanhService.getAlertForFGNotScheduledFor30Days("80", "V1");
            if (listFG != null && !listFG.isEmpty()) {
                StringBuilder contentBuilder = new StringBuilder();
                String title = "ATV_FGs not scheduled for more than 30 days";
                List<String> toPeople = Arrays.asList("Thanh.Truongcong@amkor.com");
                contentBuilder.append("<h1>List of FGs below have not been scheduled for more than 30 days. Please review it!</h1>");
                for (String fg: listFG) {
                    contentBuilder.append("<p> + ").append(fg).append("</p>");
                }

                thanhService.sendMailProcess(title, contentBuilder.toString(), toPeople, new ArrayList<>(), new ArrayList<>());
            }
            log.info("end sending email to alert fg...");
        } catch (Exception ex) {
            log.error("error sending email alert fg: {}", ex.getMessage());
        }
    }
}
