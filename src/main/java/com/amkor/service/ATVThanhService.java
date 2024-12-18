package com.amkor.service;

import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.ApiLoggingModel;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.ProcessNoteModel;
import com.amkor.service.iService.IATVThanhService;
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
import java.util.*;

@Service
public class ATVThanhService implements IATVThanhService {
    private static final Logger log = LoggerFactory.getLogger(ATVThanhService.class);

    @Override
    public boolean sendMailProcess(String title, String sContent, List<String> toPeople, List<String> ccPeople, List<String> fileNames) {

        String ENCODE = "UTF8";
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

            // from address
            msg.setFrom(new InternetAddress(fromAddress));

            Vector toList = new Vector();
            Vector ccList = new Vector();
            Vector files = new Vector();

            // To
            for (String to : toPeople) {
                toList.addElement(new InternetAddress(to)); // Array to Vector
            }
            Address[] toAddrList = new Address[toList.size()];
            toList.copyInto(toAddrList);
            msg.setRecipients(Message.RecipientType.TO, toAddrList);
            toList.clear();

            // CC
            if (ccPeople != null) {
                for (String cc : ccPeople) {
                    if (!cc.trim().isEmpty()) {
                        ccList.addElement(new InternetAddress(cc));
                    }
                }
            }
            Address[] ccAddrList = new Address[ccList.size()];
            ccList.copyInto(ccAddrList);
            msg.setRecipients(Message.RecipientType.CC, ccAddrList);
            ccList.clear();

            // Title
            msg.setSubject(title, ENCODE);

            // File Attach
            Multipart mp = new MimeMultipart();

            // add body message
            MimeBodyPart contentPart = new MimeBodyPart();

            StringBuilder content = new StringBuilder();
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

    @Override
    public int createProcessNote(ProcessNoteModel model) {
        int result = 0;
        Connection m_conn;
        PreparedStatement m_psmt;
        long currentDateTime = this.get400CurrentDate();
        try {
            Class.forName(this.getDriver());
            m_conn = DriverManager.getConnection(this.getURL(SharedConstValue.AMKOR_SHORTNAME), this.getUserID(SharedConstValue.AMKOR_SHORTNAME), this.getPasswd(SharedConstValue.AMKOR_SHORTNAME));

            String sQuery = "insert into EPLIB.EPENOTP values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
            m_psmt.setInt(i++, model.getFactoryId());
            m_psmt.setString(i++, model.getClassify().trim());
            m_psmt.setInt(i++, model.getCustomerId());
            m_psmt.setString(i++, model.getPkg().trim());
            m_psmt.setString(i++, model.getDim().trim());
            m_psmt.setString(i++, model.getLead().trim());
            m_psmt.setString(i++, model.getTargetDevice().trim());
            m_psmt.setString(i++, model.getOptionId().trim());
            m_psmt.setInt(i++, model.getOperation());
            m_psmt.setInt(i++, model.getSeq());
            m_psmt.setString(i++, model.getEngNote());
            m_psmt.setLong(i++, currentDateTime);
            m_psmt.setLong(i++, 0);
            m_psmt.setString(i, model.getUserBadge());

            result = m_psmt.executeUpdate();

            m_psmt.close();
            m_conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }


    @Override
    public int updateProcessNote(ProcessNoteModel model) {
        int result = 0;
        Connection m_conn;
        PreparedStatement m_psmt;
        long currentDateTime = this.get400CurrentDate();
        try {
            String sQuery = "update EPLIB.EPENOTP set ENNOTE = ?, ENMNDT = ?, ENTUSR = ? where ENFCID = ? AND ENCLAS = ? AND ENCUST = ? AND ENPKGE = ? " +
                    "AND ENDMSN = ? AND ENLEAD = ? AND ENDEVC = ? AND ENOPID = ? AND ENOPER = ? AND ENSEQ# = ?";
            Class.forName(this.getDriver());
            m_conn = DriverManager.getConnection(getURL(SharedConstValue.AMKOR_SHORTNAME), getUserID(SharedConstValue.AMKOR_SHORTNAME), getPasswd(SharedConstValue.AMKOR_SHORTNAME));
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
            m_psmt.setString(i++, model.getEngNote());
            m_psmt.setLong(i++, currentDateTime);
            m_psmt.setString(i++, model.getUserBadge());
            m_psmt.setInt(i++, model.getFactoryId());
            m_psmt.setString(i++, model.getClassify());
            m_psmt.setInt(i++, model.getCustomerId());
            m_psmt.setString(i++, model.getPkg());
            m_psmt.setString(i++, model.getDim());
            m_psmt.setString(i++, model.getLead());
            m_psmt.setString(i++, model.getTargetDevice());
            m_psmt.setString(i++, model.getOptionId());
            m_psmt.setInt(i++, model.getOperation());
            m_psmt.setInt(i, model.getSeq());
            result = m_psmt.executeUpdate();

            m_psmt.close();
            m_conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }

    @Override
    public int createAutoLabelMaintenance(AutoLabelModel model) {
        PreparedStatement m_pstmt;
        int record = 0;
        long currentDateTime = this.getDateTime();
        try {
            Class.forName(this.getDriver());
            Connection conn = DriverManager.getConnection(this.getURL(SharedConstValue.AMKOR_SHORTNAME), this.getUserID(SharedConstValue.AMKOR_SHORTNAME), this.getPasswd(SharedConstValue.AMKOR_SHORTNAME));
            String sQuery = "insert into EMLIB.EAUTOLBLVP values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_pstmt = conn.prepareStatement(sQuery);
            int i = 0;
            m_pstmt.setInt(++i, model.getFactoryId());
            m_pstmt.setInt(++i, model.getSiteId());
            m_pstmt.setString(++i, model.getBusinessType());
            m_pstmt.setInt(++i, model.getCustomerId());
            m_pstmt.setString(++i, model.getPkg());
            m_pstmt.setString(++i, model.getDim());
            m_pstmt.setString(++i, model.getLead());
            m_pstmt.setString(++i, model.getTargetDevice());
            m_pstmt.setString(++i, model.getKeyField1());
            m_pstmt.setString(++i, model.getKeyField2());
            m_pstmt.setString(++i, model.getFieldName());
            m_pstmt.setString(++i, model.getFieldValue());
            m_pstmt.setLong(++i, currentDateTime);
            m_pstmt.setInt(++i, model.getUserBadge());
            m_pstmt.setLong(++i, 0);
            m_pstmt.setInt(++i, 0);
            record = m_pstmt.executeUpdate();

            conn.close();
            m_pstmt.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return record;
    }

    @Override
    public int updateAutoLabel(AutoLabelModel model) {
        int result = 0;
        if (!model.getFieldName().trim().equalsIgnoreCase("lblq") && !model.getFieldName().trim().equalsIgnoreCase("unitq")) {
            return 0;
        }
        Connection m_conn;
        PreparedStatement m_psmt;
        long currentDateTime = this.getDateTime();
        try {
            String sQuery = "update EMLIB.EAUTOLBLVP set FIELD_VALUE = ?, CHANGE_TIMESTAMO = ?, CHANGE_USER = ? where FACTORY_ID = ? AND SITE_ID = ? AND BUSINESS_TYPE = ? " +
                    "AND CUSTOMER = ? AND PACKAGE = ? AND DIMENSION = ? AND LEAD = ? AND TARGET_DEVICE = ? " +
                    "AND KEY_FIELD1 = ? AND KEY_FIELD2 = ? AND FIELD_NAME = ?";
            Class.forName(this.getDriver());
            m_conn = DriverManager.getConnection(getURL(SharedConstValue.AMKOR_SHORTNAME), getUserID(SharedConstValue.AMKOR_SHORTNAME), getPasswd(SharedConstValue.AMKOR_SHORTNAME));
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
            m_psmt.setString(i++, model.getFieldValue());
            m_psmt.setLong(i++, currentDateTime);
            m_psmt.setInt(i++, model.getUserBadge());
            m_psmt.setInt(i++, model.getFactoryId());
            m_psmt.setInt(i++, model.getSiteId());
            m_psmt.setString(i++, model.getBusinessType());
            m_psmt.setInt(i++, model.getCustomerId());
            m_psmt.setString(i++, model.getPkg());
            m_psmt.setString(i++, model.getDim());
            m_psmt.setString(i++, model.getLead());
            m_psmt.setString(i++, model.getTargetDevice());
            m_psmt.setString(i++, model.getKeyField1());
            m_psmt.setString(i++, model.getKeyField2());
            m_psmt.setString(i, model.getFieldName());
            result = m_psmt.executeUpdate();

            m_conn.close();
            m_psmt.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }

    @Override
    public int addApiLogging(ApiLoggingModel model) {
        PreparedStatement m_pstmt;
        int record = 0;
        try {
            Class.forName(this.getDriver());
            Connection conn = DriverManager.getConnection(this.getURL(SharedConstValue.AMKOR_SHORTNAME), this.getUserID(SharedConstValue.AMKOR_SHORTNAME), this.getPasswd(SharedConstValue.AMKOR_SHORTNAME));

            String sQuery = "insert into EMLIB.EMESLP04 values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_pstmt = conn.prepareStatement(sQuery);
            int i = 1;

            m_pstmt.setInt(i++, model.getCifcid());
            m_pstmt.setInt(i++, model.getCiasid());
            m_pstmt.setString(i++, model.getCistn());
            m_pstmt.setLong(i++, model.getCiamkr());
            m_pstmt.setInt(i++, model.getCisub());
            m_pstmt.setString(i++, model.getCibztp());
            m_pstmt.setString(i++, model.getCists());
            m_pstmt.setFloat(i++, model.getCiseq());
            m_pstmt.setInt(i++, model.getCiopr());
            m_pstmt.setString(i++, model.getCichfd());
            m_pstmt.setString(i++, model.getCiogvl());
            m_pstmt.setString(i++, model.getCinwvl());
            m_pstmt.setString(i++, model.getCirsn());
            m_pstmt.setInt(i++, model.getCichbg());
            m_pstmt.setLong(i++, model.getCichdt());
            m_pstmt.setLong(i++, model.getCirqdt());
            m_pstmt.setString(i++, model.getCirqpg());
            m_pstmt.setInt(i++, model.getCirqbg());
            m_pstmt.setLong(i++, model.getCiacdt());
            m_pstmt.setString(i++, model.getCiacpg());
            m_pstmt.setInt(i, model.getCiacbg());

            record = m_pstmt.executeUpdate();

            m_pstmt.close();
            conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return record;
    }
}
