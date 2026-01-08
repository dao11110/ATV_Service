package com.amkor.common;


import com.amkor.common.utils.CommonUtils;
import com.amkor.common.utils.SharedConstValue;
import com.amkor.common.utils.Utils;
import com.amkor.models.ATVNetMiscTableModel;
import com.amkor.models.AlertForFGModel;
import com.amkor.service.ATVNetMiscTableService;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.ITFAService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
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

    @Scheduled(cron = "0 0 * * * *")
    public void gatherTNRDefectDatafromInternalFTPtoSFTP() {
        String host = "intl-sftp.qorvo.com";
        int port = 22;
        String user = "Amkor";
        String privateKeyPath = "D:\\NewCompression\\Files\\qorvo_defect_report\\amkor_atv.pem"; // converted from .ppk
        String passphrase = "4mk0rATV5FT9";
        String localPathDownload = "D:\\NewCompression\\Files\\qorvo_defect_report";
        List<File> filesToPush = new ArrayList<>();
        try {
            log.info("start scanning defect list report...");
            // --- Step 1: Connect to FTP ---
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect("10.201.10.165", 21);
                ftpClient.login("V1QORVOEOL", "Matkhauftp03@");
                ftpClient.cwd("in/wip/TNR_Data");

                String[] files = ftpClient.listNames();
                for (String file : files) {
                    LocalDateTime fileTime = Utils.parseDefectReportFileName(file);
                    if (fileTime.isBefore(LocalDateTime.now().minusHours(72))) {
                        log.info("File older than 72h: {}", file);
                        // --- Step 2: Download file from FTP to local temp ---
                        File localFile = new File(localPathDownload + File.separator + file);
                        try (OutputStream out = new FileOutputStream(localFile)) {
                            if (ftpClient.retrieveFile(file, out)) {
                                filesToPush.add(localFile);

                                log.info("Downloaded: {}", file);

                                // --- Step 3: Delete file from FTP after successful download ---
                                boolean deleted = ftpClient.deleteFile(file);
                                if (deleted) {
                                    log.info("Deleted from FTP: {}", file);
                                } else {
                                    log.warn("Failed to delete from FTP: {}", file);
                                }

                            }
                        }
                    }
                }

                ftpClient.logout();
                ftpClient.disconnect();

            } catch (IOException ex) {
                log.error("FTP error", ex);
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    log.error("Error disconnecting FTP", ex);
                }
            }

            try {
                JSch jsch = new JSch();
                jsch.addIdentity(privateKeyPath, passphrase);

                Session session = jsch.getSession(user, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
                sftpChannel.connect();
                sftpChannel.cd("in/wip/TNR_Data");

                // Upload all files in one session
                for (File localFile : filesToPush) {
                    sftpChannel.put(localFile.getAbsolutePath(), localFile.getName());
                    log.info("Uploaded: {}", localFile.getName());
                }

                sftpChannel.disconnect();
                session.disconnect();
            } catch (Exception e) {
                log.error("SFTP error", e);
            }

            log.info("end scanning defect list report..");
        } catch (Exception ex) {
            log.error("error scanning defect list report: {}", ex.getMessage());
        }
    }
}
