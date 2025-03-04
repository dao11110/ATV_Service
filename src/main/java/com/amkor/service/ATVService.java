package com.amkor.service;


import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.AlertForFGModel;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.OnLineScheduleSheetFileModel;
import com.amkor.models.ProcessNoteModel;
import com.amkor.service.iService.IATVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling

public class ATVService implements IATVService {


    //    @Scheduled(cron = "${batch.cron.auto-send-mail-mon-to-sat")
//    @Scheduled(cron = "0 0 10 * * MON-SAT")
    public void sendMailDaily(String fileName, String fileNameString, String title) {


        try {

            List<String> listTo = new ArrayList<>();
            listTo.add("Dao.Nguyenvan@amkor.com");
            if (title.equals("Diebank Inventory Daily")) {
                listTo.add("V1BANK@amkor.com");
                listTo.add("V1EFT0064@amkor.com");
                listTo.add("V1EFT0066@amkor.com");
            } else if (title.equals("NG Store Inventory Daily")) {
                listTo.add("V1NG@amkor.com");
            } else if (title.equals("NG Store Scrap Daily")) {
                listTo.add("V1NG@amkor.com");

            } else {
                listTo.add("V1SHIP@amkor.com");
            }

            List<String> listFileName = new ArrayList<>();
            listFileName.add(fileName);
            sendMailProcess2(title + currentDate(), listTo, new ArrayList<>(), listFileName, fileNameString, true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private String currentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);
        return currentDate;
    }

    public boolean sendMailProcess(String title, List<String> toPeople, List<String> ccPeople, List<String> fileNames, String opinion)
            throws Exception {

        String ENCODE = "UTF8";
        String MIME_PLAIN = "text/plain";
        String MIME_HTML = "text/html";

//		SendMail sendMail  = new SendMail();

        try {


//            String smtpServer = "10.101.10.6";
            String smtpServer = "k5lexim01.kr.ds.amkor.com";
            String fromAddress = "ATKNET_Notification@amkor.co.kr";
            //String title = "SendMail Automation";

            String totalMime = MIME_HTML + "; charset=" + ENCODE;
//            Properties props = System.getProperties();
//            props.put("mail.smtp.host", smtpServer);
//

            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", 25);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpServer);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

//            Session session = Session.getDefaultInstance(props, null);

            Session session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("ATKNET_Notification", "S~3-widZ~k1u+ZbR8%1j=h2'VM9]Tz");
                }
            });
            MimeMessage msg = new MimeMessage(session);

            /** from address **/
            msg.setFrom(new InternetAddress(fromAddress));

            Vector toList = new Vector();
            Vector ccList = new Vector();
            Vector files = new Vector();

            /** To **/
            for (int i = 0; i < toPeople.size(); i++) {
                String to = toPeople.get(i);
                toList.addElement(new InternetAddress(to)); // Array to Vector
            }
            Address[] toAddrList = new Address[toList.size()];
            toList.copyInto(toAddrList);
            msg.setRecipients(Message.RecipientType.TO, toAddrList);
            toList.clear();

            /** CC **/
            if (ccPeople != null) {
                for (int j = 0; j < ccPeople.size(); j++) {
                    String cc = ccPeople.get(j);
                    if (!cc.trim().equals("")) {
                        ccList.addElement(new InternetAddress(cc));
                    }
                }
            }
            Address[] ccAddrList = new Address[ccList.size()];
            ccList.copyInto(ccAddrList);
            msg.setRecipients(Message.RecipientType.CC, ccAddrList);
            ccList.clear();

            /** Title **/
            msg.setSubject(title, ENCODE);

            /** File Attach **/
            Multipart mp = new MimeMultipart();

            /** add body message **/
            MimeBodyPart contentPart = new MimeBodyPart();

            StringBuffer content = new StringBuffer();
//            content.append("<br><font fact=Arial size=-1><b>[ SampleGR Notification]</b></font>");
//            content.append(
//                    "<br><font color=black face=Arial size=-1>This is the notification of data transfer to ATI</font><br><br>");
            content.append(opinion);

            String body = content.toString();
            contentPart.setContent(body, totalMime);
            mp.addBodyPart(contentPart);


            if (fileNames.size() > 0) {
                for (String file : fileNames) {
                    MimeBodyPart filePart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(file);
                    filePart.setDataHandler(new DataHandler(fds));
                    filePart.setFileName(file);
                    mp.addBodyPart(filePart);
                }
            }


            msg.setContent(mp);

            // Set some other header information
            msg.setHeader("X-Mailer", "ATKNetAutoSendMail");
            msg.setSentDate(new Date());
            // Send the message
//            Transport.send(msg);

            Transport.send(msg, "ATKNET_Notification", "S~3-widZ~k1u+ZbR8%1j=h2'VM9]Tz");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }


    public boolean sendMailProcess2(String title, List<String> toPeople, List<String> ccPeople, List<String> fileNames, String opinion, boolean isDieBank)
            throws Exception {

        String ENCODE = "UTF8";
        String MIME_PLAIN = "text/plain";
        String MIME_HTML = "text/html";
        String user = "atvzabbix";
        String password = "XR}1Y,S3NmQ]J({s#4o_#hJCuqB%fo";

//		SendMail sendMail  = new SendMail();

        try {


            String smtpServer = "v1lexim01.vn.ds.amkor.com";
//            String fromAddress = "v1lexim01.vn.ds.amkor.com";
            String fromAddress = "ATVService@amkor.com";
            //String title = "SendMail Automation";

            String totalMime = MIME_HTML + "; charset=" + ENCODE;
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
//            props.put("mail.smtp.address", "v1lexim01.vn.ds.amkor.com");
            props.put("mail.smtp.port", 25);
            props.put("mail.smtp.auth", "true");
//
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.ssl.trust", "v1lexim01.vn.ds.amkor.com");
            props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");


//            Session session = Session.getDefaultInstance(props, null);
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);

            /** from address **/
            msg.setFrom(new InternetAddress(fromAddress));

            Vector toList = new Vector();
            Vector ccList = new Vector();
            Vector files = new Vector();

            /** To **/
            for (int i = 0; i < toPeople.size(); i++) {
                String to = toPeople.get(i);
                toList.addElement(new InternetAddress(to)); // Array to Vector
            }
            Address[] toAddrList = new Address[toList.size()];
            toList.copyInto(toAddrList);
            msg.setRecipients(Message.RecipientType.TO, toAddrList);
            toList.clear();

            /** CC **/
            if (ccPeople != null) {
                for (int j = 0; j < ccPeople.size(); j++) {
                    String cc = ccPeople.get(j);
                    if (!cc.trim().equals("")) {
                        ccList.addElement(new InternetAddress(cc));
                    }
                }
            }
            Address[] ccAddrList = new Address[ccList.size()];
            ccList.copyInto(ccAddrList);
            msg.setRecipients(Message.RecipientType.CC, ccAddrList);
            ccList.clear();

            /** Title **/
            msg.setSubject(title, ENCODE);

            /** File Attach **/
            Multipart mp = new MimeMultipart();

            /** add body message **/
            MimeBodyPart contentPart = new MimeBodyPart();

            StringBuffer content = new StringBuffer();
//            content.append("<br><font fact=Arial size=-1><b>[ATV Service Auto Sendmail]</b></font>");
//            content.append(
//                    "<br><font color=black face=Arial size=-1>This is the notification of data transfer to ATI</font><br><br>");
            content.append("<br></br>" + title);

            String body = content.toString();
            contentPart.setContent(body, totalMime);
            mp.addBodyPart(contentPart);


            if (fileNames.size() > 0) {
                for (String file : fileNames) {

                    MimeBodyPart filePart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(file);
                    filePart.setDataHandler(new DataHandler(fds));
                    if (isDieBank) {
                        filePart.setFileName(opinion);
                    } else
                        filePart.setFileName(file);
                    mp.addBodyPart(filePart);
                }
            }


            // create the Multipart and add its parts to it


            // add the Multipart to the message
            msg.setContent(mp);

            // Set some other header information
            msg.setHeader("X-Mailer", "ATVServiceAutoSendMail");
            msg.setSentDate(new Date());
            // Send the message
            Transport.send(msg, user, password);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<AlertForFGModel> getAlertForFGNotScheduledFor30Days(int factoryId, String plant, String cust) {
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        List<AlertForFGModel> result = new ArrayList<>();
        try {
            long lToday = this.getDate();
            Date releasedDate = Date.from(Instant.now().minus(Duration.ofDays(90)));  // released date 3 months ago
            long lReleasedDate = this.getDate(releasedDate);
            Date scheduledDate = Date.from(Instant.now().minus(Duration.ofDays(30)));  // scheduled date 1 month ago
            long lScheduledDate = this.getDateTime(scheduledDate);
            String sQuery = "SELECT DISTINCT XMTLNO, XPV, SSDEVC " +
                    "FROM EMLIB.ASCHMP03 " +
                    "JOIN EMLIB.EMESTP02 ON SSFCID = CVFCID AND SSASID = CVASID AND CVAMKR = SSWAMK AND SSSUB# = CVSUB# AND CVBZTP = SSBZTP " +
                    "JOIN EMLIB.XREFPOP ON SSFCID = XFCID AND SSASID =XASID AND SSWAMK =XAMKID AND SSSUB# = XSUBID AND XBZTYP = SUBSTRING(SSBZTP,0,2) " +
                    "JOIN EMLIB.INPOP ON XPONO = IPONO AND XMTLNO = IMTLNO AND XSONO = ISONO " +
                    "WHERE SSFCID = " + factoryId + " AND XPLNT = '" + plant + "' AND ICUST = " + cust + " " +
                    "AND XMTLNO in (SELECT DISTINCT IMTLNO FROM EMLIB.INPOP i WHERE i.IRLSDT >= " + lReleasedDate + " AND i.IRLSDT <= " + lToday + " AND i.IF_STATUS in ('OK','DLV','ONG', 'DON')) " +
                    "AND SSLTCD = '' " +
                    "AND CVMDUL='SCHEDULE' AND ((CVFLDN = 'NPIFLAG' AND CVFLDV = '') OR (CVFLDN = 'TNPIFLAG' AND CVFLDV = 'N')) " +
                    "AND XMTLNO not in (SELECT DISTINCT XMTLNO " +
                    "                   FROM EMLIB.ASCHMP03 " +
                    "                   JOIN EMLIB.EMESTP02 ON SSFCID = CVFCID AND SSASID = CVASID AND CVAMKR = SSWAMK AND SSSUB# = CVSUB# AND CVBZTP = SSBZTP" +
                    "                   JOIN EMLIB.XREFPOP ON SSFCID = XFCID AND SSASID =XASID AND SSWAMK =XAMKID AND SSSUB# = XSUBID AND XBZTYP = SUBSTRING(SSBZTP,0,2) " +
                    "                   WHERE SSFCID = " + factoryId + " AND XPLNT = '" + plant + "' " +
                    "                   AND XMTLNO in (SELECT DISTINCT IMTLNO FROM EMLIB.INPOP i WHERE i.IRLSDT >= " + lReleasedDate + " AND i.IRLSDT <= " + lToday + " AND i.IF_STATUS in ('OK','DLV','ONG', 'DON')) " +
                    "                   AND SSLTCD = '' " +
                    "                   AND CVMDUL='SCHEDULE' AND ((CVFLDN = 'NPIFLAG' AND CVFLDV = '') OR (CVFLDN = 'TNPIFLAG' AND CVFLDV = 'N')) " +
                    "                   AND ((SUBSTRING(SSBZTP,0,2) = 'A' AND (SSSCHD > " + lScheduledDate + " OR SSSCHD = 0)) OR (SUBSTRING(SSBZTP,0,2) = 'T' AND (SSWIDT > " + lScheduledDate + " OR SSWIDT = 0))))";
            m_conn = getConnection();
            m_psmt = m_conn.prepareStatement(sQuery);
            m_rs = m_psmt.executeQuery();
            while (m_rs.next()) {
                AlertForFGModel alert = new AlertForFGModel();
                alert.setFgCode(m_rs.getString("XMTLNO").trim());
                alert.setPv(m_rs.getString("XPV").trim());
                alert.setTargetDevice(m_rs.getString("SSDEVC").trim());
                result.add(alert);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }
        return result;
    }
}
