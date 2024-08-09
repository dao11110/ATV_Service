package com.amkor.controller.v1;

import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.*;
import com.amkor.service.ATVThanhService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@Slf4j
@RestController
public class Data400ThanhController {

    @Autowired
    private ATVThanhService thanhService;

    @RequestMapping(method = RequestMethod.POST, value = "/data400/{site}/custProductionInfoFgJson")
    @CrossOrigin(origins = "*")
    public HashMap<String, String> uploadCustProductionInfoFgJson(@PathVariable("site") String site,
                                                                  @RequestBody Map<String, Object> jsonObject) {
        HashMap<String, String> result = new HashMap<>();
        String msg = "";
        ApiLoggingModel logging = new ApiLoggingModel();
        try {
            Map<String, Object> jFgChar = (Map<String, Object>) jsonObject.get("fg_char");
            Class.forName(thanhService.getDriver());
            Connection conn = DriverManager.getConnection(thanhService.getURL(site), thanhService.getUserID(site), thanhService.getPasswd(site));
            Statement stmt = conn.createStatement();

            int cust = 78;
            String plant = "V1";
            String user = (String) jFgChar.remove("modified_user");
            String jFg = jFgChar.remove("FG").toString();
            String jPv = jFgChar.remove("PV").toString();

            CustProductionInfoFgJsonModel charValue = null;

            Iterator<String> keys = jFgChar.keySet().iterator();
            while (keys.hasNext()) {
                charValue = new CustProductionInfoFgJsonModel();
                String key = keys.next().trim();
                String value = (String) jFgChar.get(key);

                charValue.setFgName(key);
                charValue.setNewValue(value.trim());

                // get character from FG
                String characterPrevalue = this.getCharacterFromFG(conn, jFg, jPv, plant, charValue.getFgName());
                charValue.setPreValue(characterPrevalue);

                String if_timeext = this.getMaxIf_Timeext(conn, cust, jFg, jPv);
                int record = this.saveFGSpecific(conn, cust, plant, jFg, jPv, if_timeext,
                        charValue.getFgName(), charValue.getNewValue(), user);
                if (record == 0) {
                    msg = "Save failed !!";
                    break;
                }
                msg = "SUCCESS";

                // logging
                logging.setCifcid(Integer.parseInt(thanhService.getFactoryID(site)));
                logging.setCiasid(Integer.parseInt(thanhService.getSiteID(site)));
                logging.setCichdt(thanhService.getDateTime());
                logging.setCichbg(Integer.parseInt(user));
                logging.setCiogvl("API_custProductionInfoFgJson");
                logging.setCinwvl("FG CHAR Change");
                logging.setCirsn("logForAPI");
                this.addApiLogging(logging, site);
            }

            conn.close();
            stmt.close();

        } catch (Exception ex) {
            result.put("msg", ex.getMessage());
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return if_timeext.trim();
    }

    public int saveFGSpecific(Connection m_conn, int nCust, String sPlant, String sFg, String sPv, String if_timeext, String sCcName, String sCcValue, String user) {
        PreparedStatement m_pstmt = null;
        ResultSet m_rs = null;

        int nRec = 0;

        try {
            long currentDateTime = thanhService.getDateTime();

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
                sQuery = "update eplib.fgmtlct set ccvalu=?, if_user=?, if_status=? where cplnt=? And cmtlno=? And cpv=? and if_timeext=? and ccname=?";
                m_pstmt = m_conn.prepareStatement(sQuery);

                i = 1;

                m_pstmt.setString(i++, sCcValue.trim());
                m_pstmt.setString(i++, user.trim());
                m_pstmt.setString(i++, "API");
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
        PreparedStatement m_pstmt;
        StringBuilder msg = new StringBuilder();
        int[] results;
        ApiLoggingModel logging = new ApiLoggingModel();
        try {
            Class.forName(thanhService.getDriver());
            Connection conn = DriverManager.getConnection(thanhService.getURL(site), thanhService.getUserID(site), thanhService.getPasswd(site));

            long currentDateTime = thanhService.get400CurrentDate();

            String sQuery = "insert into EPLIB.EPENOTP values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_pstmt = conn.prepareStatement(sQuery);

            for (ProcessNoteModel processNoteModel : model) {
                int i = 1;
                m_pstmt.setInt(i++, processNoteModel.getFactoryId());
                m_pstmt.setString(i++, processNoteModel.getClassify().trim());
                m_pstmt.setInt(i++, processNoteModel.getCustomerId());
                m_pstmt.setString(i++, processNoteModel.getPkg().trim());
                m_pstmt.setString(i++, processNoteModel.getDim().trim());
                m_pstmt.setString(i++, processNoteModel.getLead().trim());
                m_pstmt.setString(i++, processNoteModel.getTargetDevice().trim());
                m_pstmt.setString(i++, processNoteModel.getOptionId().trim());
                m_pstmt.setInt(i++, processNoteModel.getOperation());
                m_pstmt.setInt(i++, processNoteModel.getSeq());
                m_pstmt.setString(i++, processNoteModel.getEngNote());
                m_pstmt.setLong(i++, currentDateTime);
                m_pstmt.setLong(i++, 0);
                m_pstmt.setString(i, processNoteModel.getUserBadge());

                m_pstmt.addBatch();

                // logging
                logging.setCifcid(processNoteModel.getFactoryId());
                logging.setCiasid(Integer.parseInt(thanhService.getSiteID(site)));
                logging.setCichdt(thanhService.getDateTime());
                logging.setCichbg(Integer.parseInt(processNoteModel.getUserBadge()));
                logging.setCiogvl("API_createProcessNote");
                logging.setCinwvl("Process Note create");
                logging.setCirsn("logForAPI");
                this.addApiLogging(logging, site);
            }


            results = m_pstmt.executeBatch();
            for (int i = 0; i < results.length; i++) {
                if (results[i] == 0) {
                    msg.append("FAILED TO ADD ELEMENT #").append(i).append("; ");
                }
            }
            if (msg.length() == 0) {
                msg.append("SUCCESS");
            }
            result.put("msg", msg.toString());
            conn.close();
            m_pstmt.close();

        } catch (Exception ex) {
            result.put("msg", ex.getMessage());
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data400/{site}/createAutoLabelMaintenance")
    @CrossOrigin(origins = "*")
    public HashMap<String, String> createAutoLabelMaintenance(@PathVariable("site") String site,
                                                              @RequestBody AutoLabelModel model) {
        HashMap<String, String> result = new HashMap<>();
        PreparedStatement m_pstmt;
        String msg;
        int record;
        ApiLoggingModel logging = new ApiLoggingModel();
        try {
            Class.forName(thanhService.getDriver());
            Connection conn = DriverManager.getConnection(thanhService.getURL(site), thanhService.getUserID(site), thanhService.getPasswd(site));

            long currentDateTime = thanhService.getDateTime();
            String sQuery = "insert into EMLIB.EAUTOLBLVP values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_pstmt = conn.prepareStatement(sQuery);
            int i = 0;
            m_pstmt.setInt(++i, model.getFactoryId());
			m_pstmt.setInt(++i,model.getSiteId());
			m_pstmt.setString(++i,model.getBusinessType());
			m_pstmt.setInt(++i,model.getCustomerId());
			m_pstmt.setString(++i,model.getPkg());
			m_pstmt.setString(++i,model.getDim());
			m_pstmt.setString(++i,model.getLead());
			m_pstmt.setString(++i,model.getTargetDevice());
			m_pstmt.setString(++i,model.getKeyField1());
			m_pstmt.setString(++i,model.getKeyField2());
			m_pstmt.setString(++i,model.getFieldName());
			m_pstmt.setString(++i,model.getFieldValue());
			m_pstmt.setLong(++i,currentDateTime);
			m_pstmt.setInt(++i,model.getUserBadge());
			m_pstmt.setLong(++i,0);
			m_pstmt.setInt(++i,0);
            record = m_pstmt.executeUpdate();
            if (record == 0) {
                msg = "FAILED TO ADD";
            } else {
                msg = "SUCCESS";
            }
            // logging
            logging.setCifcid(model.getFactoryId());
            logging.setCiasid(model.getSiteId());
            logging.setCichdt(currentDateTime);
            logging.setCichbg(model.getUserBadge());
            logging.setCiogvl("API_createAutoLabelMaintenance");
            logging.setCinwvl("Auto Label Maintenance create");
            logging.setCirsn("logForAPI");
            this.addApiLogging(logging, site);
            result.put("msg", msg);

            m_pstmt.close();
            conn.close();

        } catch (Exception ex) {
            result.put("msg", ex.getMessage());
        }

        return result;
    }

    public void addApiLogging(ApiLoggingModel model, String site) throws ClassNotFoundException, SQLException {
        PreparedStatement m_pstmt;
        Class.forName(thanhService.getDriver());
        Connection conn = DriverManager.getConnection(thanhService.getURL(site), thanhService.getUserID(site), thanhService.getPasswd(site));

        String sQuery = "insert into EMLIB.EMESLP04 values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        m_pstmt = conn.prepareStatement(sQuery);
        int i=1;

        m_pstmt.setInt(i++, model.getCifcid());
        m_pstmt.setInt(i++,model.getCiasid());
        m_pstmt.setString(i++,model.getCistn());
        m_pstmt.setLong(i++,model.getCiamkr());
        m_pstmt.setInt(i++,model.getCisub());
        m_pstmt.setString(i++,model.getCibztp());
        m_pstmt.setString(i++,model.getCists());
        m_pstmt.setFloat(i++,model.getCiseq());
        m_pstmt.setInt(i++,model.getCiopr());
        m_pstmt.setString(i++,model.getCichfd());
        m_pstmt.setString(i++,model.getCiogvl());
        m_pstmt.setString(i++,model.getCinwvl());
        m_pstmt.setString(i++,model.getCirsn());
        m_pstmt.setInt(i++,model.getCichbg());
        m_pstmt.setLong(i++,model.getCichdt());
        m_pstmt.setLong(i++,model.getCirqdt());
        m_pstmt.setString(i++,model.getCirqpg());
        m_pstmt.setInt(i++,model.getCirqbg());
        m_pstmt.setLong(i++,model.getCiacdt());
        m_pstmt.setString(i++, model.getCiacpg());
        m_pstmt.setInt(i,model.getCiacbg());

        m_pstmt.executeUpdate();

        m_pstmt.close();
        conn.close();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/temptemptemp")
    public void alertFGExceed30Days() {
        try {
            log.info("start sending email to alert fg...");
            List<AlertForFGModel> listFG = thanhService.getAlertForFGNotScheduledFor30Days(SharedConstValue.FACTORY_ID, SharedConstValue.PLANT);
            if (listFG != null && !listFG.isEmpty()) {
                StringBuilder contentBuilder = new StringBuilder();
                String title = "ATV_FGs not scheduled for more than 30 days";
                List<String> toPeople = Arrays.asList("Thanh.Truongcong@amkor.com");
                contentBuilder.append("<h2>List of FGs below have not been scheduled for more than 30 days. Please review it!</h2>");
                contentBuilder.append("<table style='border: 1px solid black'>");
                contentBuilder.append("<tr style='border: 1px solid black'><th style='border: 1px solid black'>FG</th><th style='border: 1px solid black'>PV</th></tr>");
                for (AlertForFGModel alert: listFG) {
                    String rowContent = String.format("<tr style='border: 1px solid black'><td style='border: 1px solid black'>%s</td><td style='border: 1px solid black'>%s</td></tr>", alert.getFgCode(), alert.getPv());
                    contentBuilder.append(rowContent);
                }
                contentBuilder.append("</table>");

                thanhService.sendMailProcess(title, contentBuilder.toString(), toPeople, new ArrayList<>(), new ArrayList<>());
            }
            log.info("end sending email to alert fg...");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

}
