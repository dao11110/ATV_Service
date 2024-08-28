package com.amkor.common;


import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.AlertForFGModel;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.IATVThanhService;
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
    private IATVService iatvService;

    @Autowired
    private IATVThanhService iatvThanhService;

    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void sendMailAlertFGNotScheduledIn30Days() {
        try {
            log.info("start sending email to alert fg...");
            List<AlertForFGModel> listFG = iatvService.getAlertForFGNotScheduledFor30Days(SharedConstValue.FACTORY_ID, SharedConstValue.PLANT);
            if (listFG != null && !listFG.isEmpty()) {
                StringBuilder contentBuilder = new StringBuilder();
                String title = "ATV_FGs not scheduled for more than 30 days";
                List<String> toPeople = Arrays.asList("Thanh.Truongcong@amkor.com", "Nam.Nguyenhoang@amkor.com");
                contentBuilder.append("<h2>List of FGs below have not been scheduled for more than 30 days. Please review it!</h2>");
                contentBuilder.append("<table style='border: 1px solid black'>");
                contentBuilder.append("<tr style='border: 1px solid black'><th style='border: 1px solid black'>FG</th><th style='border: 1px solid black'>PV</th></tr>");
                for (AlertForFGModel alert : listFG) {
                    String rowContent = String.format("<tr style='border: 1px solid black'><td style='border: 1px solid black'>%s</td><td style='border: 1px solid black'>%s</td></tr>", alert.getFgCode(), alert.getPv());
                    contentBuilder.append(rowContent);
                }
                contentBuilder.append("</table>");

                iatvThanhService.sendMailProcess(title, contentBuilder.toString(), toPeople, new ArrayList<>(), new ArrayList<>());
            }
            log.info("end sending email to alert fg...");
        } catch (Exception ex) {
            log.error("error sending email alert fg: {}", ex.getMessage());
        }
    }
}
