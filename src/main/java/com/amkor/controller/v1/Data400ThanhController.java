package com.amkor.controller.v1;

import com.amkor.service.ATVService;
import com.amkor.service.ATVThanhService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class Data400ThanhController {


    @Autowired
    private ATVThanhService thanhService;

    @RequestMapping(method = RequestMethod.GET, value = "/temptemptemp")
    public void alertFGExceed30Days() {
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
            log.error(ex.getMessage());
        }

    }

}
