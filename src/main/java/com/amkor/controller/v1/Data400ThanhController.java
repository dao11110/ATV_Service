package com.amkor.controller.v1;

import com.amkor.models.*;
import com.amkor.service.APILoggingService;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.IATVThanhService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@Slf4j
@RestController
public class Data400ThanhController {

    @Autowired
    private IATVThanhService iatvThanhService;

    @Autowired
    private IATVService iatvService;

    @Autowired
    private APILoggingService apiLoggingService;

    private static final String UPDATE_FAIL_MESSAGE = "FAILED TO UPDATE";
    private static final String CREATE_FAIL_MESSAGE = "FAILED TO CREATE";
    private static final String SUCCESS_MESSAGE = "SUCCESS";

    @RequestMapping(method = RequestMethod.POST, value = "/data400/{site}/custProductionInfoFgJson")
    @CrossOrigin(origins = "*")
    public HashMap<String, String> uploadCustProductionInfoFgJson(@PathVariable("site") String site,
                                                                  @RequestBody Map<String, Object> jsonObject) {
        HashMap<String, String> result = new HashMap<>();
        String msg = "";
        ApiLoggingModel logging = new ApiLoggingModel();
        StringBuilder logContent = new StringBuilder("{");
        long currentDateTime = this.iatvService.getDateTime();
        try {
            Map<String, Object> jFgChar = (Map<String, Object>) jsonObject.get("fg_char");
            Class.forName(iatvThanhService.getDriver());
            Connection conn = DriverManager.getConnection(iatvThanhService.getURL(site), iatvThanhService.getUserID(site), iatvThanhService.getPasswd(site));
            Statement stmt = conn.createStatement();

            int cust = 78;
            String plant = "V1";
            String user = (String) jFgChar.remove("modified_user");
            String jFg = jFgChar.remove("FG").toString();
            String jPv = jFgChar.remove("PV").toString();

            CustProductionInfoFgJsonModel charValue;

            Iterator<String> keys = jFgChar.keySet().iterator();
            String if_timeext = this.getMaxIf_Timeext(conn, cust, jFg, jPv);
            while (keys.hasNext()) {

                charValue = new CustProductionInfoFgJsonModel();
                String key = keys.next().trim();
                String value = (String) jFgChar.get(key);
                logContent.append(key).append("=").append(value).append(", ");

                charValue.setFgName(key);
                charValue.setNewValue(value.trim());

                // get character from FG
                String characterPrevalue = this.getCharacterFromFG(conn, jFg, jPv, plant, charValue.getFgName());
                charValue.setPreValue(characterPrevalue);

                int record = this.saveFGSpecific(conn, cust, plant, jFg, jPv, if_timeext,
                        charValue.getFgName(), charValue.getNewValue(), user);
                if (record == 0) {
                    msg = "Save failed !!";
                    break;
                }
                msg = SUCCESS_MESSAGE;
            }

            logContent.append("}");

            // logging
            logging.setCifcid(Integer.parseInt(iatvService.getFactoryID(site)));
            logging.setCiasid(Integer.parseInt(iatvService.getSiteID(site)));
            logging.setCichdt(currentDateTime);
            logging.setCichbg(Integer.parseInt(user));
            logging.setCiogvl("API_custProductionInfoFgJson");
            logging.setCinwvl("FG CHAR Change");
            logging.setCirsn("logForAPI");
            this.iatvThanhService.addApiLogging(logging);
            this.apiLoggingService.insertLog(new ATVNetAPILoggingModel(
                    user,
                    currentDateTime,
                    logContent.toString(),
                    "FG CHAR Change"
            ));

            conn.close();
            stmt.close();

        } catch (Exception ex) {
            result.put("msg", ex.getMessage());
            return result;
        }
        result.put("msg", msg);
        return result;
    }

    public String getCharacterFromFG(Connection conn, String fg, String pv, String plant, String fieldName) {
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;
        String IF_TIMEEXT = "";
        String flowCode = "";
        try {
            String sQuery = "SELECT IF_TIMEEXT, FPLNT FROM EPLIB.FGMTLHT WHERE FMTLNO = ? AND FPV = ? AND FPLNT = ? order by IF_TIMEEXT desc";

            m_pstmt = conn.prepareStatement(sQuery);

            m_pstmt.setString(1, fg.trim());
            m_pstmt.setString(2, pv.trim());
            m_pstmt.setString(3, plant.trim());

            m_rs = m_pstmt.executeQuery();
            if (m_rs.next()) {
                IF_TIMEEXT = m_rs.getString("IF_TIMEEXT").trim();
            }

            if (m_pstmt != null) m_pstmt.close();
            if (m_rs != null) m_rs.close();
            m_pstmt = null;
            m_rs = null;
            if (!IF_TIMEEXT.trim().isEmpty()) {
                sQuery = "SELECT * FROM EPLIB.FGMTLCT WHERE CPLNT = ? AND CMTLNO = ? AND CPV = ? AND CCNAME = ? AND IF_TIMEEXT = ?";

                m_pstmt = conn.prepareStatement(sQuery);

                m_pstmt.setString(1, plant);
                m_pstmt.setString(2, fg.trim());
                m_pstmt.setString(3, pv.trim());
                m_pstmt.setString(4, fieldName.trim());
                m_pstmt.setString(5, IF_TIMEEXT);

                m_rs = m_pstmt.executeQuery();

                while (m_rs.next()) {
                    if (m_rs.getString("CCNAME").trim().equals(fieldName.trim())) {
                        flowCode = m_rs.getString("CCVALU").trim();
                    }
                }

                m_rs.close();
                m_pstmt.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            flowCode = "";
        }

        return flowCode;
    }

    public String getMaxIf_Timeext(Connection m_conn, int nTargetCust, String sFg, String sPv) {
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;

        String if_timeext = "";

        try {

            String sQuery = "select if_timeext from EPLIB.FGMTLHT "
                    + "where fmtlno=? And fpv=?  "
                    + "order by if_timeext desc  "
                    + "fetch first rows only ";

            m_pstmt = m_conn.prepareStatement(sQuery);

            int i = 1;

            m_pstmt.setString(i++, sFg.trim());
            m_pstmt.setString(i++, sPv.trim());

            m_rs = m_pstmt.executeQuery();

            if (m_rs.next()) {
                if_timeext = m_rs.getString("if_timeext");
            }

            m_rs.close();
            m_pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return if_timeext.trim();
    }

    public int saveFGSpecific(Connection m_conn, int nCust, String sPlant, String sFg, String sPv, String if_timeext, String sCcName, String sCcValue, String user) {
        PreparedStatement m_pstmt;
        ResultSet m_rs;

        int nRec = 0;

        try {
            long currentDateTime = iatvService.getDateTime();

            String sQuery = "select * from eplib.fgmtlct where cplnt=? And cmtlno=? And cpv=? and if_timeext=? and ccname=? ";

            m_pstmt = m_conn.prepareStatement(sQuery);

            int i = 1;

            m_pstmt.setString(i++, sPlant.trim());
            m_pstmt.setString(i++, sFg.trim());
            m_pstmt.setString(i++, sPv.trim());
            m_pstmt.setString(i++, if_timeext.trim());
            m_pstmt.setString(i++, sCcName.trim());

            m_rs = m_pstmt.executeQuery();

            if (m_rs.next()) {
                sQuery = "update eplib.fgmtlct set ccvalu=?, if_user=?, if_status=?, if_errdesc=? where cplnt=? And cmtlno=? And cpv=? and if_timeext=? and ccname=?";
                m_pstmt = m_conn.prepareStatement(sQuery);

                i = 1;

                m_pstmt.setString(i++, sCcValue.trim());
                m_pstmt.setString(i++, user.trim());
                m_pstmt.setString(i++, "API");
                m_pstmt.setString(i++, "MES_FGCHAR");
                m_pstmt.setString(i++, sPlant.trim());
                m_pstmt.setString(i++, sFg.trim());
                m_pstmt.setString(i++, sPv.trim());
                m_pstmt.setString(i++, if_timeext.trim());
                m_pstmt.setString(i++, sCcName.trim());

                nRec = m_pstmt.executeUpdate();
            } else {
                sQuery = "insert into EPLIB.FGMTLCT VALUES (?,?,?,?,?,?,?,?,?,?)";

                m_pstmt = m_conn.prepareStatement(sQuery);

                i = 1;
                m_pstmt.setString(i++, sPlant.trim());
                m_pstmt.setString(i++, sFg.trim());
                m_pstmt.setString(i++, sPv.trim());
                m_pstmt.setString(i++, sCcName.trim());
                m_pstmt.setString(i++, sCcValue.trim());
                m_pstmt.setString(i++, if_timeext.trim());
                m_pstmt.setLong(i++, currentDateTime);
                m_pstmt.setString(i++, "API");
                m_pstmt.setString(i++, "MES_FGCHAR");
                m_pstmt.setString(i++, user.trim());

                nRec = m_pstmt.executeUpdate();

                m_pstmt.close();
                m_rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nRec;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data400/{site}/createProcessNote")
    @CrossOrigin(origins = "*")
    public HashMap<String, String> createProcessNote(@PathVariable("site") String site,
                                                     @RequestBody ProcessNoteModel[] model) {
        HashMap<String, String> result = new HashMap<>();
        String msg = "";
        String note;
        ApiLoggingModel logging = new ApiLoggingModel();
        try {
            long current = iatvService.getDateTime();
            for (ProcessNoteModel processNoteModel : model) {
                int record;
                if (iatvService.checkExistProcessNote(processNoteModel)) {
                    record = iatvThanhService.updateProcessNote(processNoteModel);
                    logging.setCinwvl("Process Note update");
                    note = "Process Note update";
                    if (record == 0) {
                        msg = UPDATE_FAIL_MESSAGE;
                    }

                } else {
                    record = iatvThanhService.createProcessNote(processNoteModel);
                    logging.setCinwvl("Process Note create");
                    note = "Process Note create";
                    if (record == 0) {
                        msg = CREATE_FAIL_MESSAGE;
                    }
                }

                // logging
                logging.setCifcid(processNoteModel.getFactoryId());
                logging.setCiasid(Integer.parseInt(iatvThanhService.getSiteID(site)));
                logging.setCichdt(current++);
                logging.setCichbg(Integer.parseInt(processNoteModel.getUserBadge()));
                logging.setCiogvl("API_createProcessNote");
                logging.setCirsn("logForAPI");
                this.iatvThanhService.addApiLogging(logging);
                this.apiLoggingService.insertLog(new ATVNetAPILoggingModel(
                        String.valueOf(processNoteModel.getUserBadge()),
                        iatvService.getDateTime(),
                        processNoteModel.toString(),
                        note
                ));

                if (record == 0) {
                    break;
                }
            }
            if (msg.isEmpty()) {
                msg = SUCCESS_MESSAGE;
            }
            result.put("msg", msg);

        } catch (Exception ex) {
            msg = ex.getMessage();
        }

        result.put("msg", msg);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data400/{site}/createAutoLabelMaintenance")
    @CrossOrigin(origins = "*")
    public HashMap<String, String> createAutoLabelMaintenance(@PathVariable("site") String site,
                                                              @RequestBody AutoLabelModel model) {
        HashMap<String, String> result = new HashMap<>();

        String msg = "";
        String note;
        int record;
        long currentDateTime = iatvService.getDateTime();
        ApiLoggingModel logging = new ApiLoggingModel();
        try {
            if (iatvService.checkExistAutoLabel(model)) {
                record = iatvThanhService.updateAutoLabel(model);
                if (record == 0) {
                    msg = UPDATE_FAIL_MESSAGE;
                }
                logging.setCinwvl("Auto Label update");
                note = "Auto Label update";
            } else {
                record = iatvThanhService.createAutoLabelMaintenance(model);
                if (record == 0) {
                    msg = CREATE_FAIL_MESSAGE;
                }
                logging.setCinwvl("Auto Label create");
                note = "Auto Label create";
            }

            // logging
            logging.setCifcid(model.getFactoryId());
            logging.setCiasid(model.getSiteId());
            logging.setCichdt(currentDateTime);
            logging.setCichbg(model.getUserBadge());
            logging.setCiogvl("API_createAutoLabelMaintenance");
            logging.setCirsn("logForAPI");
            this.iatvThanhService.addApiLogging(logging);
            this.apiLoggingService.insertLog(new ATVNetAPILoggingModel(
                    String.valueOf(model.getUserBadge()),
                    currentDateTime,
                    model.toString(),
                    note
            ));
            if (msg.isEmpty()) {
                msg = SUCCESS_MESSAGE;
            }
        } catch (Exception ex) {
            msg = ex.getMessage();
        }
        result.put("msg", msg);
        return result;
    }

}
