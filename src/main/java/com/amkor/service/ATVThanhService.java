package com.amkor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ATVThanhService {
    private static final String DRIVER = "com.ibm.as400.access.AS400JDBCDriver";
    private static final Logger log = LoggerFactory.getLogger(ATVThanhService.class);

    public String getDriver() {
        return DRIVER;
    }

    public String getURL(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "jdbc:as400://10.101.6.12";
                break;
            case "ATV":
                result = "jdbc:as400://10.201.6.21";
                break;
        }
        return result;
    }

    public String getUserID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "pruser";
                break;
            case "ATV":
                result = "MESPGMR";
                break;
        }
        return result;
    }

    public String getPasswd(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "prod0504";
                break;
            case "ATV":
                result = "gloryah";
                break;
        }
        return result;
    }

    public String getPPOMSTP(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "PPSLIB.PPOMSTP";
                break;
            case "ATV":
                result = "EMLIB.PPOMSTP";
                break;
        }
        return result;
    }

    public String getLibrary(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "PPSLIB";
                break;
            case "ATV":
                result = "EMLIB";
                break;
        }
        return result;
    }

    public String getFactoryID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "1";
                break;
            case "ATV":
                result = "80";
                break;
        }
        return result;
    }

    public String getSiteID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "1";
                break;
            case "ATV":
                result = "1";
                break;
        }
        return result;
    }

    public boolean sendMailProcess(String title, String sContent, List<String> toPeople, List<String> ccPeople, List<String> fileNames)
            throws Exception {

        String ENCODE = "UTF8";
        String MIME_PLAIN = "text/plain";
        String MIME_HTML = "text/html";
        String user = "atvzabbix";
        String password = "XR}1Y,S3NmQ]J({s#4o_#hJCuqB%fo";

        try {

            String smtpServer = "v1lexim01.vn.ds.amkor.com";
            String fromAddress = "ATVService@amkor.com";

            String totalMime = MIME_HTML + "; charset=" + ENCODE;
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", 25);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpServer);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");


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
            content.append(sContent);

            String body = content.toString();
            contentPart.setContent(body, totalMime);
            mp.addBodyPart(contentPart);


            if (!fileNames.isEmpty()) {
                for (String file : fileNames) {
                    MimeBodyPart filePart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(file);
                    filePart.setDataHandler(new DataHandler(fds));
                    filePart.setFileName(file);
                    mp.addBodyPart(filePart);
                }
            }


            msg.setContent(mp);

            msg.setHeader("X-Mailer", "ATV-Service");
            msg.setSentDate(new Date());

            Transport.send(msg, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public long getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String strDate = sdf.format(now);
        return Long.parseLong(strDate);
    }

     public long getDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(d);
        return Long.parseLong(strDate);
    }

    public long getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String strDate = sdf.format(now);
        return Long.parseLong(strDate);
    }
    public long getDateTime(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(d);
        return Long.parseLong(strDate);
    }

    public long get400CurrentDate() {
        String current = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long result = Long.parseLong(current) - 19000000000000L;
        return result;
    }

    public List<String> getAlertForFGNotScheduledFor30Days(String factoryId, String plant) {
        Connection m_conn;
        PreparedStatement m_psmt;
        ResultSet m_rs;
        List<String> result = null;
        HashSet<String> setString = new HashSet<>();
        try {
            long today = this.getDate();
            Date dateThreeMonthsAgo = Date.from(Instant.now().minus(Duration.ofDays(90)));
            long threeMonthsAgo = this.getDate(dateThreeMonthsAgo);
            long dateTimeThreeMonthsAgo = this.getDateTime(dateThreeMonthsAgo);
            String sQuery = "SELECT DISTINCT XMTLNO " +
                    "FROM EMLIB.ASCHMP03 " +
                    "JOIN EMLIB.EMESTP02 ON SSFCID = CVFCID AND SSASID = CVASID AND CVAMKR = SSWAMK AND SSSUB# = CVSUB# " +
                    "JOIN EMLIB.XREFPOP ON SSFCID = XFCID AND SSASID =XASID AND SSWAMK =XAMKID AND SSSUB# = XSUBID " +
                    "JOIN EPLIB.EPXPINP ON XMTLNO = MTLNO " +
                    "WHERE SSFCID = " + factoryId + " AND XPLNT = '" + plant + "' " +
                    "AND XMTLNO in (SELECT DISTINCT IMTLNO FROM EMLIB.INPOP i WHERE i.IRLSDT >= " + threeMonthsAgo + " AND i.IRLSDT <= " + today + " AND i.IF_STATUS = 'DON') " +
                    "AND SSLTCD = '' " +
                    "AND CVMDUL='SCHEDULE' AND CVFLDN = 'NPIFLAG' AND CVFLDV = '' AND CVOPR# = 0 " +
                    "AND (SSSCHD <= " + dateTimeThreeMonthsAgo + " or SSWIDT <= " + dateTimeThreeMonthsAgo + ")";
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            m_psmt = m_conn.prepareStatement(sQuery);
            m_rs = m_psmt.executeQuery();
            while (m_rs.next()) {
                setString.add(m_rs.getString("XMTLNO").trim());
            }
            result = new ArrayList<>(setString);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }
}