package com.amkor.service.impl;

import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.OnLineScheduleSheetFileModel;
import com.amkor.models.ProcessNoteModel;
import com.amkor.models.ScheduleMasterModel;
import com.amkor.service.iService.ITFAService;
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
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.amkor.common.utils.*;

@Service
public class TFAServiceImpl implements ITFAService {
    private static final Logger log = LoggerFactory.getLogger(TFAServiceImpl.class);

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
    public boolean checkExistAutoLabel(AutoLabelModel model) {
        boolean result = false;
        Connection m_conn;
        PreparedStatement m_psmt;
        ResultSet m_rs;
        try {
            String sQuery = "select * from EMLIB.EAUTOLBLVP where FACTORY_ID = ? AND SITE_ID = ? AND BUSINESS_TYPE = ? " +
                    "AND CUSTOMER = ? AND PACKAGE = ? AND DIMENSION = ? AND LEAD = ? AND TARGET_DEVICE = ? " +
                    "AND KEY_FIELD1 = ? AND KEY_FIELD2 = ? AND FIELD_NAME = ?";
            Class.forName(this.getDriver());
            m_conn = DriverManager.getConnection(getURL(SharedConstValue.AMKOR_SHORTNAME), getUserID(SharedConstValue.AMKOR_SHORTNAME), getPasswd(SharedConstValue.AMKOR_SHORTNAME));
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
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
            m_psmt.setString(i++, model.getFieldName());
            m_rs = m_psmt.executeQuery();
            if (m_rs.next()) {
                result = true;
            }

            m_conn.close();
            m_psmt.close();
            m_rs.close();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


        return result;
    }

    @Override
    public OnLineScheduleSheetFileModel getOnlineScheduleSheetMemoFileFromStationAndLotName(String station, String lotName) {
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        OnLineScheduleSheetFileModel onLineScheduleSheetFileModel = null;
        try {
            String sQuery = "SELECT * FROM EMLIB.EMESTP032 " +
                    "join EMLIB.ASCHMP02 on FACTORY_ID = SMFCID AND RECORD_ID = SMSCH# " +
                    "WHERE FACTORY_ID = " + SharedConstValue.FACTORY_ID + " AND TYPE_ID = 'S' AND SMLOT# = '" + lotName + "' AND ( " +
                    "EXISTS (SELECT 1 FROM EMLIB.EMESTP032 WHERE SMLOT# = '" + lotName + "' AND FILE_X LIKE '%" + station + "%') " +
                    "OR FILE_X LIKE '%" + station + "%')";
            m_conn = getConnection();
            m_psmt = m_conn.prepareStatement(sQuery);
            m_rs = m_psmt.executeQuery();
            if (m_rs.next()) {
                onLineScheduleSheetFileModel = new OnLineScheduleSheetFileModel();

                onLineScheduleSheetFileModel.setFactoryID(m_rs.getInt(1));
                onLineScheduleSheetFileModel.setType(m_rs.getString(2));
                onLineScheduleSheetFileModel.setRecordID(m_rs.getString(3));
                onLineScheduleSheetFileModel.setPath(m_rs.getString(4));
                onLineScheduleSheetFileModel.setFile(m_rs.getString(5));
                onLineScheduleSheetFileModel.setEffectiveTo(m_rs.getLong(6));
                onLineScheduleSheetFileModel.setCreateDateTime(m_rs.getLong(7));
                onLineScheduleSheetFileModel.setCreateBadge(m_rs.getString(8));
                onLineScheduleSheetFileModel.setMaintDateTime(m_rs.getLong(9));
                onLineScheduleSheetFileModel.setMaintBadge(m_rs.getString(10));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }

        return onLineScheduleSheetFileModel;
    }

    @Override
    public boolean checkExistProcessNote(ProcessNoteModel model) {

        boolean result = false;
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        try {
            String sQuery = "select * from EPLIB.EPENOTP where ENFCID = ? AND ENCLAS = ? AND ENCUST = ? AND ENPKGE = ? " +
                    "AND ENDMSN = ? AND ENLEAD = ? AND ENDEVC = ? AND ENOPID = ? AND ENOPER = ? AND ENSEQ# = ?";
            m_conn = getConnection();
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
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
            m_rs = m_psmt.executeQuery();
            if (m_rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }

        return result;
    }

    @Override
    public String holdLot(String lotName, String lotDcc, String holdCode, String holdReason, String userBadge) {
        Connection m_conn = null;
        CallableStatement m_cstmt = null;
        ResultSet m_rs = null;
        String msg = "";
        try {
            ScheduleMasterModel model = getScheduleMasterByLotNameDcc(lotName, lotDcc);
            if (model != null) {
                m_conn = getConnection();
                String sProcedure = "{call EMLIB.ESCHSP26 (?,?,?,?,?,?,?,?,?,?,?)}";
                m_cstmt = m_conn.prepareCall(sProcedure);

                m_cstmt.setString(1, CommonUtils.getString("H", 1));
                m_cstmt.setString(2, CommonUtils.getString(model.getFactoryId(), 3));
                m_cstmt.setString(3, CommonUtils.getString(model.getSiteId(), 3));
                m_cstmt.setString(4, CommonUtils.getString(model.getAmkId(), 20));
                m_cstmt.setString(5, CommonUtils.getString(model.getSubId(), 5));
                m_cstmt.setString(6, CommonUtils.getString(holdCode, 4));
                m_cstmt.setString(7, CommonUtils.getString(holdReason, 50));
                m_cstmt.setString(8, CommonUtils.getString(0, 8));
                m_cstmt.setString(9, CommonUtils.getString(userBadge, 7));
                m_cstmt.setString(10, CommonUtils.getString(getDateTime(), 14));
                m_cstmt.setString(11, CommonUtils.getString(holdReason, 50));
                m_cstmt.execute();
                msg = "success";
            } else {
                msg = "No lot found";
            }
        } catch (Exception ex) {
            msg = "exception";
        } finally {
            cleanUp(m_conn, m_cstmt, m_rs);
        }

        return msg;
    }

    public ScheduleMasterModel getScheduleMasterByLotNameDcc(String lotName, String dcc) {
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        ScheduleMasterModel result = null;
        try {
            String sQuery = "SELECT * FROM EMLIB.ASCHMP02 WHERE SMLOT# = ? AND SMDCC = ?";
            m_conn = getConnection();
            m_psmt = m_conn.prepareStatement(sQuery);
            m_psmt.setString(1, lotName);
            m_psmt.setString(2, dcc);
            m_rs = m_psmt.executeQuery();
            while (m_rs.next()) {
                result = new ScheduleMasterModel();
                result.setFactoryId(m_rs.getInt("SMFCID"));
                result.setSiteId(m_rs.getInt("SMASID"));
                result.setAmkId(m_rs.getInt("SMWAMK"));
                result.setSubId(m_rs.getInt("SMSUB#"));
                result.setLotName(lotName);
                result.setLotDcc(dcc);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }

        return result;
    }
}
