package com.amkor.service.impl;

import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.*;
import com.amkor.service.ATVNetMiscTableService;
import com.amkor.service.iService.ITFAService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ATVNetMiscTableService miscTableService;

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
            String sQuery = "update EPLIB.EPENOTP set ENNOTE = ?, ENMNDT = ?, ENTUSR = ? where ENFCID = ? AND ENCLAS = ? AND ENCUST = ? AND ENPKGE = ? " + "AND ENDMSN = ? AND ENLEAD = ? AND ENDEVC = ? AND ENOPID = ? AND ENOPER = ? AND ENSEQ# = ?";
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
            String sQuery = "update EMLIB.EAUTOLBLVP set FIELD_VALUE = ?, CHANGE_TIMESTAMO = ?, CHANGE_USER = ? where FACTORY_ID = ? AND SITE_ID = ? AND BUSINESS_TYPE = ? " + "AND CUSTOMER = ? AND PACKAGE = ? AND DIMENSION = ? AND LEAD = ? AND TARGET_DEVICE = ? " + "AND KEY_FIELD1 = ? AND KEY_FIELD2 = ? AND FIELD_NAME = ?";
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
            String sQuery = "select * from EMLIB.EAUTOLBLVP where FACTORY_ID = ? AND SITE_ID = ? AND BUSINESS_TYPE = ? " + "AND CUSTOMER = ? AND PACKAGE = ? AND DIMENSION = ? AND LEAD = ? AND TARGET_DEVICE = ? " + "AND KEY_FIELD1 = ? AND KEY_FIELD2 = ? AND FIELD_NAME = ?";
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
            String sQuery = "SELECT * FROM EMLIB.EMESTP032 " + "join EMLIB.ASCHMP02 on FACTORY_ID = SMFCID AND RECORD_ID = SMSCH# " + "WHERE FACTORY_ID = " + SharedConstValue.FACTORY_ID + " AND TYPE_ID = 'S' AND SMLOT# = '" + lotName + "' AND ( " + "EXISTS (SELECT 1 FROM EMLIB.EMESTP032 WHERE SMLOT# = '" + lotName + "' AND FILE_X LIKE '%" + station + "%') " + "OR FILE_X LIKE '%" + station + "%')";
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
            String sQuery = "select * from EPLIB.EPENOTP where ENFCID = ? AND ENCLAS = ? AND ENCUST = ? AND ENPKGE = ? " + "AND ENDMSN = ? AND ENLEAD = ? AND ENDEVC = ? AND ENOPID = ? AND ENOPER = ? AND ENSEQ# = ?";
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

    @Override
    public String releaseLot(String lotName, String lotDcc, String holdCode, String releaseReason, String userBadge, int holdOpr, long shipBackDate) {
        String msg = "";
        try {
            ScheduleMasterModel model = getScheduleMasterByLotNameDcc(lotName, lotDcc);

            if (model != null) {
                eMESUserModel user = getEMESUserInformation(model.getFactoryId(), userBadge.trim());
                HoldReleaseLogModel holdRecord = getHoldReleaseLog(model.getFactoryId(), model.getSiteId(), model.getAmkId(), model.getSubId(), holdOpr);
                ScheduleSubMasterModel subMasterModel = getScheduleSubMasterById(model.getAmkId(), model.getSubId(), "ASSY");
                if (subMasterModel == null) {
                    subMasterModel = getScheduleSubMasterById(model.getAmkId(), model.getSubId(), "TEST");
                }

                if (subMasterModel == null) {
                    msg = "No sub master record found";
                    return msg;
                }

                // validation before release
                // 1. get hold record and user
                if (holdRecord == null) {
                    msg = "No hold record found";
                    return msg;
                }
                if (user == null) {
                    msg = "Not found user with badge: " + userBadge;
                    return msg;
                }

                String holdRemark = holdRecord.getHoldRemark().trim();
                String userGroup = user.getUserGroup().trim();

                // 7C release must be released in eMES
                if (holdCode.equals("7C")) {
                    msg = "7C release must be released in eMES due to rfmd request authorization";
                    return msg;
                }

                // validate ship back date
                if (shipBackDate <= 0 || !validateDB2DateTime(holdRecord.getFactoryID(), shipBackDate)) {
                    msg = "shipBackDate is invalid";
                    return msg;
                }

                // WA release must be with release reason
                if (holdCode.equals("WA") && releaseReason.trim().isEmpty()) {
                    msg = "WA release must be with release reason";
                    return msg;
                }

                // QMR hold code cannot be released by user
                if (holdCode.equals("QMR")) {
                    msg = "QMR hold code cannot be released by user";
                    return msg;
                }

                // check user badge
                if ((holdRemark.startsWith("F*") || holdRemark.startsWith("Q*")) && !userGroup.equals("QA")) {
                    msg = "F*/Q* hold only be released by QA user";
                    return msg;
                }

                // perform release
                releaseLot(model.getFactoryId(), model.getSiteId(), model.getAmkId(), model.getSubId(), holdCode, holdRemark, releaseReason, Integer.parseInt(userBadge), subMasterModel.getShipBackDate(), holdRecord.getHoldDateTime());

                if (model.getStation().trim().equals("WIP") && model.getStatus1().trim().equals("TEST") && !releaseReason.trim().equals("")) {
                    int serial = getMaxWipProcessNoteSerial(model.getFactoryId(), model.getSiteId(), model.getAmkId(), model.getSubId());
                    WipProcessNoteModel noteModel = new WipProcessNoteModel();
                    noteModel.setFactoryId(model.getFactoryId());
                    noteModel.setSiteId(model.getSiteId());
                    noteModel.setAmkorId(model.getAmkId());
                    noteModel.setAmkorSubId(model.getSubId());
                    noteModel.setClassify("H");
                    noteModel.setEngNote(releaseReason);
                    noteModel.setSerial(serial);
                    noteModel.setCreateDateTime(this.getDateTime());
                    noteModel.setCreateBadge(Integer.parseInt(userBadge));
                    createWipProcessNote(noteModel);
                }

                realTimeShipback(model.getFactoryId(), model.getSiteId(), model.getAmkId(), model.getSubId());
            } else {
                msg = "No lot found";
            }
        } catch (Exception ex) {
            msg = ex.getMessage();
            log.error(msg);
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
                result.setStation(m_rs.getString("SMSTN"));
                result.setStatus1(m_rs.getString("SMSTS1"));
                result.setStatus2(m_rs.getString("SMSTS2"));
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

    public ScheduleSubMasterModel getScheduleSubMasterById(long amkorId, int subId, String bizType) {
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        ScheduleSubMasterModel result = null;
        try {
            String sQuery = "SELECT * FROM EMLIB.ASCHMP03 WHERE SSWAMK = ? AND SSSUB# = ? AND SSBZTP = ?";
            m_conn = getConnection();
            m_psmt = m_conn.prepareStatement(sQuery);
            m_psmt.setLong(1, amkorId);
            m_psmt.setInt(2, subId);
            m_psmt.setString(3, bizType);
            m_rs = m_psmt.executeQuery();
            while (m_rs.next()) {
                result = new ScheduleSubMasterModel();
                result.setFactoryId(m_rs.getInt("SSFCID"));
                result.setSiteId(m_rs.getInt("SSASID"));
                result.setAmkId(m_rs.getInt("SSWAMK"));
                result.setSubId(m_rs.getInt("SSSUB#"));
                result.setShipBackDate(m_rs.getLong("SSPJSB"));
                result.setDateCode(m_rs.getInt("SSDTCD"));
                result.setRvShipBackDate(m_rs.getLong("SSRVSB"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }

        return result;
    }

    @Override
    public List<DateCodeDiscrepancyModel> getDateCodeDiscrepancy() {
        List<DateCodeDiscrepancyModel> result = new ArrayList<>();
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        Connection m_conn = null;
        try {
            m_conn = getConnection();
            m_pstmt = m_conn.prepareStatement(
                    "WITH \n" +
                            "DateCode AS (\n" +
                            "    SELECT LDLOT#, LDDCC, LDDATA AS date_code\n" +
                            "    FROM PRMSLIB.LMDWNLPW\n" +
                            "    WHERE LDSEQ# = 2\n" +
                            "),\n" +
                            "MainData AS (\n" +
                            "    SELECT \n" +
                            "        A.SSWAMK, \n" +
                            "        B.SMLOT#, \n" +
                            "        A.SSSUB#, \n" +
                            "        B.SMDCC,\n" +
                            "        A.SSDTCD AS date_code_emes,\n" +
                            "        DC.date_code AS date_code_min,\n" +
                            "        A.SSSCHD,\n" +
                            "        (\n" +
                            "            SELECT SUBSTRING(SDWW, 2)\n" +
                            "            FROM CSCLIB.CCCDTMP\n" +
                            "            WHERE SDCUST = 78 \n" +
                            "              AND SDFRDT <= CONCAT('1', SUBSTRING(A.SSSCHD, 3, 6)) \n" +
                            "              AND SDTODT >= CONCAT('1', SUBSTRING(A.SSSCHD, 3, 6))\n" +
                            "        ) AS CORRECT_WW\n" +
                            "    FROM \n" +
                            "        EMLIB.ASCHMP03 A\n" +
                            "    JOIN \n" +
                            "        EMLIB.ASCHMP02 B ON A.SSWAMK = B.SMWAMK AND B.SMCSCD = 78\n" +
                            "    LEFT JOIN \n" +
                            "        DateCode DC ON B.SMLOT# = DC.LDLOT# AND B.SMDCC = DC.LDDCC\n" +
                            "    LEFT JOIN \n" +
                            "        EMLIB.AWIPMP01 W ON W.WMWAMK = A.SSWAMK\n" +
                            "    WHERE \n" +
                            "        A.SSSCHD != 0 \n" +
                            "        AND A.SSBZTP = 'ASSY'\n" +
                            "    GROUP BY \n" +
                            "        A.SSWAMK, B.SMLOT#, A.SSSUB#, B.SMDCC, A.SSDTCD, DC.date_code, A.SSSCHD\n" +
                            "    HAVING \n" +
                            "        SUM(CASE WHEN W.WMOPR# = 295 THEN 1 ELSE 0 END) = 0\n" +
                            ")\n" +
                            "SELECT *\n" +
                            "FROM MainData\n" +
                            "WHERE \n" +
                            "    date_code_emes <> date_code_min\n" +
                            "    OR CORRECT_WW <> date_code_emes\n" +
                            "    OR CORRECT_WW <> date_code_min"
            );
            m_rs = m_pstmt.executeQuery();
            while (m_rs.next()) {
                DateCodeDiscrepancyModel model = new DateCodeDiscrepancyModel();
                model.setWipAmkorId(getTrimmedString(m_rs, "SSWAMK"));
                model.setWipAmkorSubId(getTrimmedString(m_rs, "SSSUB#"));
                model.setLotName(getTrimmedString(m_rs, "SMLOT#"));
                model.setLotDcc(getTrimmedString(m_rs, "SMDCC"));
                model.setMesDateCode(getTrimmedString(m_rs, "date_code_emes"));
                model.setMinDateCode(getTrimmedString(m_rs, "date_code_min"));
                model.setCorrectDateCode(getTrimmedString(m_rs, "CORRECT_WW"));
                if (model.getMinDateCode() != null) {
                    result.add(model);
                }

                if (!model.getCorrectDateCode().isEmpty()) {
                    int record = this.updateSchSubMasterDateCode(model.getWipAmkorId(), model.getWipAmkorSubId(), model.getCorrectDateCode());
                    if (record == 0) {
                        log.error(model.getWipAmkorId());
                        log.error(model.getWipAmkorSubId());
                        log.error("failed to update sch sub master date code: ");
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, m_rs);
        }

        return result;
    }

    public String getTrimmedString(ResultSet rs, String columnLabel) throws SQLException {
        String value = rs.getString(columnLabel);
        return (value != null) ? value.trim() : null;
    }

    public int updateSchSubMasterDateCode(String wipAmkorId, String subId, String dateCode) {
        int result = 0;
        Connection m_conn = null;
        PreparedStatement m_pstmt = null;
        try {
            m_conn = getConnection();
            m_pstmt = m_conn.prepareStatement("UPDATE EMLIB.ASCHMP03 SET SSDTCD = ? WHERE SSWAMK = ? AND SSSUB# = ?");
            m_pstmt.setString(1, dateCode);
            m_pstmt.setString(2, wipAmkorId);
            m_pstmt.setString(3, subId);
            result = m_pstmt.executeUpdate();
            m_pstmt.close();
            m_conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, null);
        }
        return result;
    }

    public String getCurrentDateCode(String wipAmkorId, String wipAmkorSubId) {
        String result = "";
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        Connection m_conn = null;
        try {
            m_conn = getConnection();
            m_pstmt = m_conn.prepareStatement("SELECT SSDTCD FROM EMLIB.ASCHMP03 WHERE SSWAMK = ? AND SSSUB# = ?");
            m_pstmt.setString(1, wipAmkorId);
            m_pstmt.setString(2, wipAmkorSubId);
            m_rs = m_pstmt.executeQuery();
            while (m_rs.next()) {
                result = getTrimmedString(m_rs, "SSDTCD");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, m_rs);
        }
        return result;
    }

    public String getMinDateCode(String lotName, String dcc) {
        String result = "";
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        Connection m_conn = null;
        try {
            m_conn = getConnection();
            m_pstmt = m_conn.prepareStatement("SELECT LDDATA FROM PRMSLIB.LMDWNLPW WHERE LDLOT# = ? AND LDDCC = ?");
            m_pstmt.setString(1, lotName);
            m_pstmt.setString(2, dcc);
            m_rs = m_pstmt.executeQuery();
            while (m_rs.next()) {
                result = getTrimmedString(m_rs, "LDDATA");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, m_rs);
        }
        return result;
    }

    public String sendMailReportDateCodeDiscrepancyChecking(Map<String, Object> body) {
        String result = "failed";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> toList = new ArrayList<>();
            List<String> ccList = Arrays.asList("Thanh.Truongcong@amkor.com", "Richard.Lacbay@amkor.com");

            List<ATVNetMiscTableModel> records = miscTableService.getList(
                    SharedConstValue.FACTORY_ID,
                    "MAIL_ALERT_DATECODE",
                    SharedConstValue.PLANT,
                    SharedConstValue.CUST_CODE_KIOXIA);

            for (ATVNetMiscTableModel record : records) {
                String[] recipients = record.getLongDesc().split(";");
                if (record.getShortDesc().equals("to")) {
                    toList.addAll(Arrays.asList(recipients));
                } else {
                    ccList.addAll(Arrays.asList(recipients));
                }
            }

            List<DateCodeDiscrepancyModel> listData = objectMapper.convertValue(body.get("result"), new TypeReference<List<DateCodeDiscrepancyModel>>() {
            });
            String title = "No Date Code Discrepancy";
            String content = "<h2>Dear Team, </br> There is no date code discrepancy. </br></h2>";
            if (!listData.isEmpty()) {
                title = "Alert: Date Code Discrepancy Checking";
                content = createMailBody(listData);
            }
            sendMailProcess(title, content, toList, ccList, new ArrayList<>());
            result = "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    public String createMailBody(List<DateCodeDiscrepancyModel> discrepancies) {
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<!DOCTYPE html>");
        mailBody.append("<html lang=\"en\">");
        mailBody.append("<head>");
        mailBody.append("<meta charset=\"UTF-8\">");
        mailBody.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        mailBody.append("<title>Date Code Comparison</title>");
        mailBody.append("<style>");
        mailBody.append("table { width: 100%; border-collapse: collapse; }");
        mailBody.append("th, td { border: 1px solid #ddd; padding: 8px; }");
        mailBody.append("th { background-color: #f2f2f2; text-align: left; }");
        mailBody.append("</style>");
        mailBody.append("</head>");
        mailBody.append("<body>");
        mailBody.append("<h2>Dear Team, </br> Here is Lot# list have date code discrepancy. </br></h2>");
        mailBody.append("<h3>Date Code Comparison</h3>");
        mailBody.append("<table>");
        mailBody.append("<thead>");
        mailBody.append("<tr>");
        mailBody.append("<th>No</th>");
        mailBody.append("<th>Lot Name</th>");
        mailBody.append("<th>Lot Dcc</th>");
        mailBody.append("<th>Wip Amkor Id</th>");
        mailBody.append("<th>Wip Amkor Sub Id</th>");
        mailBody.append("<th>Should be</th>");
        mailBody.append("<th>Before eMES Date Code</th>");
        mailBody.append("<th>After eMES Date Code</th>");
        mailBody.append("<th>Before MIN Date Code</th>");
        mailBody.append("<th>After MIN Date Code</th>");
        mailBody.append("<th>Result</th>");
        mailBody.append("</tr>");
        mailBody.append("</thead>");
        mailBody.append("<tbody>");

        for (int i = 0; i < discrepancies.size(); i++) {
            DateCodeDiscrepancyModel discrepancy = discrepancies.get(i);
            String result = "Passed";
            String currentDateCode = getCurrentDateCode(discrepancy.getWipAmkorId(), discrepancy.getWipAmkorSubId());
            String minDateCode = getMinDateCode(discrepancy.getLotName(), discrepancy.getLotDcc());
            if (discrepancy.getMesDateCode().equals(currentDateCode) &&
                    discrepancy.getMinDateCode().equals(minDateCode)) {
                result = "Failed";
                holdLot(discrepancy.getLotName(), discrepancy.getLotDcc(), "5D", "Date Code discrepancy", "0");
            }

            mailBody.append("<tr>");
            mailBody.append("<td>").append(i + 1).append("</td>");
            mailBody.append("<td>").append(discrepancy.getLotName()).append("</td>");
            mailBody.append("<td>").append(discrepancy.getLotDcc()).append("</td>");
            mailBody.append("<td>").append(discrepancy.getWipAmkorId()).append("</td>");
            mailBody.append("<td>").append(discrepancy.getWipAmkorSubId()).append("</td>");
            mailBody.append("<td>").append(discrepancy.getCorrectDateCode()).append("</td>");
            mailBody.append("<td>").append(discrepancy.getMesDateCode()).append("</td>");
            mailBody.append("<td>").append(currentDateCode).append("</td>");
            mailBody.append("<td>").append(discrepancy.getMinDateCode()).append("</td>");
            mailBody.append("<td>").append(minDateCode).append("</td>");
            mailBody.append("<td>").append(result).append("</td>");
            mailBody.append("</tr>");
        }

        mailBody.append("</tbody>");
        mailBody.append("</table>");
        mailBody.append("</body>");
        mailBody.append("</html>");

        return mailBody.toString();
    }

    public HoldReleaseLogModel getHoldReleaseLog(int factoryId, int siteId, long amkorId, int amkorSubId, int holdOpr) {
        HoldReleaseLogModel setVO = null;
        Connection m_conn = null;
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        try {
            m_conn = getConnection();
            String sQuery = "SELECT * FROM EMLIB.EMESLP01 WHERE HRFCID = ? AND HRASID = ? AND HRAMKR = ? AND HRSUB# = ? " +
                    "AND HROPR# = ? AND HRSTS = 'HOLD'";
            m_pstmt = m_conn.prepareStatement(sQuery);

            m_pstmt.setInt(1, factoryId);
            m_pstmt.setInt(2, siteId);
            m_pstmt.setLong(3, amkorId);
            m_pstmt.setInt(4, amkorSubId);
            m_pstmt.setInt(5, holdOpr);

            m_rs = m_pstmt.executeQuery();
            if (m_rs.next()) {
                setVO = new HoldReleaseLogModel();
                int nCol = 0;
                setVO.setFactoryID(m_rs.getInt(++nCol));
                setVO.setSiteID(m_rs.getInt(++nCol));
                setVO.setStationCode(m_rs.getString(++nCol));
                setVO.setAmkorID(m_rs.getLong(++nCol));
                setVO.setAmkorSubID(m_rs.getInt(++nCol));
                setVO.setStatus(m_rs.getString(++nCol));
                setVO.setFutureHoldRequested(m_rs.getString(++nCol));
                setVO.setOperationStep(m_rs.getFloat(++nCol));
                setVO.setOperation(m_rs.getInt(++nCol));
                setVO.setHoldReason(m_rs.getString(++nCol));
                setVO.setHoldRemark(m_rs.getString(++nCol));
                setVO.setHoldDateTime(m_rs.getLong(++nCol));
                setVO.setHoldProgramStamp(m_rs.getString(++nCol));
                setVO.setHoldBadge(m_rs.getInt(++nCol));
                setVO.setFutureHoldDateTime(m_rs.getLong(++nCol));
                setVO.setReleaseRemark(m_rs.getString(++nCol));
                setVO.setReleaseDateTime(m_rs.getLong(++nCol));
                setVO.setReleaseProgramStamp(m_rs.getString(++nCol));
                setVO.setReleaseBadge(m_rs.getInt(++nCol));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, m_rs);
        }

        return setVO;
    }

    public boolean validateDB2DateTime(int factoryId, long dateTime) {
        boolean result = false;
        Connection m_conn = null;
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        try {
            m_conn = getConnection();
            String sQuery = "SELECT * FROM DPTBLB.ETBDATE WHERE TDFCID = ? AND TDDAT1 = ? ";
            m_pstmt = m_conn.prepareStatement(sQuery);

            m_pstmt.setInt(1, factoryId);
            m_pstmt.setLong(2, dateTime);

            m_rs = m_pstmt.executeQuery();
            if (m_rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    public eMESUserModel getEMESUserInformation(int factoryId, String badge) {
        eMESUserModel user = null;
        Connection m_conn = null;
        PreparedStatement m_pstmt = null;
        ResultSet rs = null;
        try {
            m_conn = getConnection();
            String sQuery = "SELECT * FROM DPTBLB.ETBUSR01 WHERE UPFCID = ? AND UPBADG = ?";
            m_pstmt = m_conn.prepareStatement(sQuery);

            m_pstmt.setInt(1, factoryId);
            m_pstmt.setString(2, badge);

            rs = m_pstmt.executeQuery();
            if (rs.next()) {
                user = new eMESUserModel();

                user.setFactoryID(rs.getInt("UPFCID"));
                user.setUserID(rs.getString("UPUSER"));
                user.setPassword(rs.getString("UPPASS"));
                user.setPassExpireDate(rs.getLong("UPPSED"));
                user.setPlant(rs.getString("UPPLNT"));
                user.setLineCode(rs.getString("UPLINE"));
                user.setOutQ(rs.getString("UPOUTQ"));
                user.setUserGroup(rs.getString("UPUSRG"));
                user.setName(rs.getString("UPNAME"));
                user.setBadge(rs.getInt("UPBADG"));
                user.setEmail(rs.getString("UPMAIL"));
                user.setOfficeTel(rs.getLong("UPPHON"));
                user.setCellTel(rs.getLong("UPCELL"));
                user.setLastLoginDateTime(rs.getLong("UPLOGI"));
                user.setLastLogoutDateTime(rs.getLong("UPLOGO"));
                user.setRemark(rs.getString("UPREMK"));
                user.setCreatedDateTime(rs.getLong("UPENDT"));
                user.setChangedDateTime(rs.getLong("UPCHDT"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_pstmt, rs);
        }

        return user;
    }

    public void releaseLot(int factoryId, int siteId, long amkId, int subId, String holdCode, String holdReason, String releaseReason, int userBadge, long shipBackDate, long holdDatetime) {
        Connection m_conn = null;
        CallableStatement m_cstmt = null;
        ResultSet m_rs = null;
        try {
            m_conn = getConnection();
            String sProcedure = "{call EMLIB.ESCHSP26 (?,?,?,?,?,?,?,?,?,?,?)}";
            m_cstmt = m_conn.prepareCall(sProcedure);

            m_cstmt.setString(1, CommonUtils.getString("R", 1));
            m_cstmt.setString(2, CommonUtils.getString(factoryId, 3));
            m_cstmt.setString(3, CommonUtils.getString(siteId, 3));
            m_cstmt.setString(4, CommonUtils.getString(amkId, 20));
            m_cstmt.setString(5, CommonUtils.getString(subId, 5));
            m_cstmt.setString(6, CommonUtils.getString(holdCode, 4));
            m_cstmt.setString(7, CommonUtils.getString(releaseReason, 50));
            m_cstmt.setString(8, CommonUtils.getString(shipBackDate, 8));
            m_cstmt.setString(9, CommonUtils.getString(userBadge, 7));
            m_cstmt.setString(10, CommonUtils.getString(holdDatetime, 14));
            m_cstmt.setString(11, CommonUtils.getString(holdReason, 50));
            m_cstmt.execute();
            m_cstmt.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            cleanUp(m_conn, m_cstmt, m_rs);
        }
    }

    public int createWipProcessNote(WipProcessNoteModel model) {
        int nResult = 0;

        Connection m_conn = null;
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;

        try {
            m_conn = getConnection();
            String sQuery = "INSERT  INTO EMLIB.ENOTESP VALUES(?,?,?,?,?,?,?,?,?)";
            m_pstmt = m_conn.prepareStatement(sQuery);

            int i = 0;
            m_pstmt.setInt(++i, model.getFactoryId());
            m_pstmt.setInt(++i, model.getSiteId());
            m_pstmt.setLong(++i, model.getAmkorId());
            m_pstmt.setInt(++i, model.getAmkorSubId());
            m_pstmt.setInt(++i, model.getSerial());
            m_pstmt.setString(++i, model.getClassify());
            m_pstmt.setString(++i, model.getEngNote());
            m_pstmt.setLong(++i, model.getCreateDateTime());
            m_pstmt.setInt(++i, model.getCreateBadge());

            nResult = m_pstmt.executeUpdate();

        } catch (Exception e) {
            String sMsg = e.getMessage();
            log.error(sMsg);
            System.out.println(sMsg);
        } finally {
            cleanUp(m_conn, m_pstmt, m_rs);
        }

        return nResult;
    }

    public int getMaxWipProcessNoteSerial(int factoryId, int siteId, long wipAmkorId, int wipAmkorSubId) {
        int result = 0;
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        try {
            Class.forName(this.getDriver());
            m_conn = DriverManager.getConnection(this.getURL(SharedConstValue.AMKOR_SHORTNAME), this.getUserID(SharedConstValue.AMKOR_SHORTNAME), this.getPasswd(SharedConstValue.AMKOR_SHORTNAME));

            String sQuery = "select MAX(NTSRL#) as NTMAX from EMLIB.ENOTESP where NTFCID = ? AND NTASID = ? AND NTWAMK = ? AND NTSUB# = ?";
            m_psmt = m_conn.prepareStatement(sQuery);
            int i = 1;
            m_psmt.setInt(i++, factoryId);
            m_psmt.setInt(i++, siteId);
            m_psmt.setLong(i++, wipAmkorId);
            m_psmt.setInt(i, wipAmkorSubId);

            m_rs = m_psmt.executeQuery();
            if (m_rs.next()) {
                result = m_rs.getInt("NTMAX") + 1;
            }

            m_psmt.close();
            m_conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            cleanUp(m_conn, m_psmt, m_rs);
        }

        return result;
    }

    public void realTimeShipback(int nFactoryId, int nSiteId, long nAmkorId, int nSub) {
        Connection m_conn = null;
        CallableStatement m_cstmt = null;
        try {
            m_conn = getConnection();

            String sProcedure = "{call EMLIB.EMESFODSP (?,?,?,?)}";

            m_cstmt = m_conn.prepareCall(sProcedure);

            m_cstmt.setString(1, CommonUtils.getString(nFactoryId, 3));
            m_cstmt.setString(2, CommonUtils.getString(nSiteId, 3));
            m_cstmt.setString(3, CommonUtils.getString(nAmkorId, 20));
            m_cstmt.setString(4, CommonUtils.getString(nSub, 5));

            m_cstmt.execute();

            m_cstmt.close();

        } catch (Exception e) {
            String sMsg = e.getMessage();
            log.error(sMsg);
            System.out.println(sMsg);
        } finally {
            cleanUp(m_conn, m_cstmt, null);
        }
    }

    public Map<String, Object> getDefectListByLotName(String lotName) {
        Connection m_conn = null;
        Map<String, Object> result = new HashMap<>();
        PreparedStatement m_psmt = null;
        ResultSet m_rs = null;
        int totalRejectQty = 0;
        try {
            m_conn = getConnection();

            // get defect list by lot name
            String sQuery = "select DFFDES, SUM(W2DQTY) as TotalQty from emlib.awipmp01\n" +
                    "join emlib.aschmp02 on wmwamk = smwamk and wmsub# = smsub#\n" +
                    "join emlib.ewiplp02 on wmwamk = w2wamk and wmsub# = w2wsub and wmseq# = w2step\n" +
                    "join DPTBLB.ETBDEFT on W2FCID=DFFCID AND W2DOPR=DFOPR# AND W2DCOD=DFDFT# and DFCUST=575 AND DFOPR# = 865\n" +
                    "where smlot# = ? and wmopr# = 865 and WMOUDT != 0\n" +
                    "group by DFFDES";

            m_psmt = m_conn.prepareStatement(sQuery);

            int i = 1;
            m_psmt.setString(i, lotName);

            m_rs = m_psmt.executeQuery();

            while (m_rs.next()) {
                totalRejectQty += m_rs.getInt("TotalQty");
                result.put(m_rs.getString("DFFDES").trim(), m_rs.getInt("TotalQty"));
            }

            // get in qty
            sQuery = "select WMINQT, SSDEVC from emlib.awipmp01\n" +
                    "join emlib.aschmp02 on wmwamk = smwamk and wmsub# = smsub#\n" +
                    "join emlib.aschmp03 on wmwamk = sswamk and wmsub# = sssub#\n" +
                    "where smlot# = ? and wmopr# = 865 and WMOUDT != 0 AND WMINQT != 0";

            m_psmt = m_conn.prepareStatement(sQuery);

            i = 1;
            m_psmt.setString(i, lotName);

            m_rs = m_psmt.executeQuery();
            if (m_rs.next()) {
                String ssdevc = m_rs.getString("SSDEVC");
                result.put("SSDEVC", ssdevc != null ? ssdevc.trim() : "");
                result.put("WMINQT", m_rs.getInt("WMINQT"));
            }

            // set total reject qty
            result.put("TotalRejectQty", totalRejectQty);

            m_psmt.close();
            m_conn.close();
        } catch (Exception e) {
            String sMsg = e.getMessage();
            log.error(sMsg);
            System.out.println(sMsg);
        } finally {
            cleanUp(m_conn, m_psmt, null);
        }
        return result;
    }
}
