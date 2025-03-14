package com.amkor.common;


import com.amkor.common.utils.CommonUtils;
import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.ATVNetMiscTableModel;
import com.amkor.models.AlertForFGModel;
import com.amkor.service.ATVNetMiscTableService;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.ITFAService;
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
    private ITFAService ITFAService;

    @Autowired
    private ATVNetMiscTableService miscTableService;

    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void sendMailAlertFGNotScheduledIn30Days() {
        try {
            log.info("start sending email to alert fg...");
            List<AlertForFGModel> listFG = iatvService.getAlertForFGNotScheduledFor30Days(SharedConstValue.FACTORY_ID, SharedConstValue.PLANT, SharedConstValue.CUST_CODE_KIOXIA);
            if (listFG != null && !listFG.isEmpty()) {
                StringBuilder contentBuilder = new StringBuilder();
                String title = "ATV_FGs not scheduled for more than 30 days";
                List<String> toPeople = new ArrayList<>();
                List<String> ccPeople = new ArrayList<>();

                List<ATVNetMiscTableModel> records = miscTableService.getList(
                        SharedConstValue.FACTORY_ID,
                        SharedConstValue.MISC_TABLE_ID_MAIL_ALERT_FG,
                        SharedConstValue.PLANT,
                        SharedConstValue.CUST_CODE_KIOXIA
                );

                for (ATVNetMiscTableModel record : records) {
                    String[] recipients = record.getLongDesc().split(";");
                    if (record.getShortDesc().equals("to")) {
                        toPeople.addAll(Arrays.asList(recipients));
                    } else {
                        ccPeople.addAll(Arrays.asList(recipients));
                    }
                }

                ATVNetMiscTableModel record = miscTableService.getOne(
                        SharedConstValue.FACTORY_ID,
                        "WHITELIST_ALERT_FG",
                        SharedConstValue.PLANT,
                        SharedConstValue.CUST_CODE_KIOXIA
                );

                String[] whitelistFgs = record.getLongDesc().split(";");

                contentBuilder.append("<h2>List of FGs below have not been scheduled for more than 30 days. Please review it!</h2>");
                contentBuilder.append("<table style='border: 1px solid black'>");
                contentBuilder.append("<tr style='border: 1px solid black'><th style='border: 1px solid black'>FG</th><th style='border: 1px solid black'>PV</th><th style='border: 1px solid black'>Target Device</th></tr>");
                for (AlertForFGModel alert : listFG) {
                    if (!CommonUtils.ArrayContains(whitelistFgs, alert.getFgCode().trim())) {
                        String rowContent = String.format("<tr style='border: 1px solid black'><td style='border: 1px solid black'>%s</td><td style='border: 1px solid black'>%s</td><td style='border: 1px solid black'>%s</td></tr>", alert.getFgCode().trim(), alert.getPv().trim(), alert.getTargetDevice().trim());
                        contentBuilder.append(rowContent);
                    }
                }
                contentBuilder.append("</table>");

                ITFAService.sendMailProcess(title, contentBuilder.toString(), toPeople, ccPeople, new ArrayList<>());
            }
            log.info("end sending email to alert fg...");
        } catch (Exception ex) {
            log.error("error sending email alert fg: {}", ex.getMessage());
        }
    }
}
