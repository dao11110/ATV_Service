package com.amkor.controller.v1;

import com.amkor.common.response.ApiResponse;
import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.*;
import com.amkor.service.APILoggingService;
import com.amkor.service.ATVNetMiscTableService;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.ITFAService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@RestController
public class TFAController {

    @Autowired
    private ITFAService ITFAService;

    @Autowired
    private IATVService iatvService;

    @Autowired
    private APILoggingService apiLoggingService;

    @Autowired
    private ATVNetMiscTableService miscTableService;

    private static final String UPDATE_FAIL_MESSAGE = "FAILED TO UPDATE";
    private static final String CREATE_FAIL_MESSAGE = "FAILED TO CREATE";
    private static final String SUCCESS_MESSAGE = "SUCCESS";
    private static final String FAILED_MESSAGE = "FAILED";

    private static final Map<String, List<String>> defectMap = new HashMap<>();

    static {
        defectMap.put("Die FM", Arrays.asList("OTHER REJECT MODE", "DIE CHIP OUT", "OTHER2"));
        defectMap.put("Surface damage", Arrays.asList("SCRATCH"));
        defectMap.put("Edge FM", Arrays.asList("EDGE FOREIGN MATERIAL"));
        defectMap.put("Side defect", Arrays.asList("SIDE DEFECT"));
        defectMap.put("Surface contamination", Arrays.asList("SURFACE CONTAMINATION"));
        defectMap.put("Mark", Arrays.asList("MARKING REJECT"));
        defectMap.put("Edge Straightness", Arrays.asList("EDGE STRAIGHTNESS/CHIP OUT"));
        defectMap.put("Coplanarity", Arrays.asList("COPLANARITY"));
        defectMap.put("IR inspection", Arrays.asList("CRACK"));
        defectMap.put("Ball quality", Arrays.asList("BALL QUALITY"));
        defectMap.put("Ball height", Arrays.asList("BALL HEIGHT"));
        defectMap.put("Matrix quality", Arrays.asList("2D BARCODE REJECT"));

    }

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
            Class.forName(ITFAService.getDriver());
            Connection conn = DriverManager.getConnection(ITFAService.getURL(site), ITFAService.getUserID(site), ITFAService.getPasswd(site));
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
            this.ITFAService.addApiLogging(logging);
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
                if (ITFAService.checkExistProcessNote(processNoteModel)) {
                    record = ITFAService.updateProcessNote(processNoteModel);
                    logging.setCinwvl("Process Note update");
                    note = "Process Note update";
                    if (record == 0) {
                        msg = UPDATE_FAIL_MESSAGE;
                    }

                } else {
                    record = ITFAService.createProcessNote(processNoteModel);
                    logging.setCinwvl("Process Note create");
                    note = "Process Note create";
                    if (record == 0) {
                        msg = CREATE_FAIL_MESSAGE;
                    }
                }

                // logging
                logging.setCifcid(processNoteModel.getFactoryId());
                logging.setCiasid(Integer.parseInt(ITFAService.getSiteID(site)));
                logging.setCichdt(current++);
                logging.setCichbg(Integer.parseInt(processNoteModel.getUserBadge()));
                logging.setCiogvl("API_createProcessNote");
                logging.setCirsn("logForAPI");
                this.ITFAService.addApiLogging(logging);
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
            if (ITFAService.checkExistAutoLabel(model)) {
                record = ITFAService.updateAutoLabel(model);
                if (record == 0) {
                    msg = UPDATE_FAIL_MESSAGE;
                }
                logging.setCinwvl("Auto Label update");
                note = "Auto Label update";
            } else {
                record = ITFAService.createAutoLabelMaintenance(model);
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
            this.ITFAService.addApiLogging(logging);
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

    @RequestMapping(method = RequestMethod.GET, value = "/data400/{site}/getScheduleSheetMemoFile")
    @CrossOrigin(origins = "*")
    public ApiResponse<List<HashMap<String, Object>>> getOnlineScheduleSheetMemoFileFromStationAndLotName(@PathVariable("site") String site,
                                                                                                          @RequestParam("lotName") String lotName,
                                                                                                          @RequestParam("station") String station,
                                                                                                          @RequestParam("userBadge") String userBadge) {
        InputStreamResource resource;
        ApiLoggingModel logging = new ApiLoggingModel();
        long currentDateTime = iatvService.getDateTime();
        String note;
        String msg;
        List<HashMap<String, Object>> data;
        try {
            OnLineScheduleSheetFileModel fileModel = ITFAService.getOnlineScheduleSheetMemoFileFromStationAndLotName(lotName, station);
            if (fileModel != null) {
                String filePath = URLEncoder.encode(fileModel.getPath() + fileModel.getFile(), StandardCharsets.UTF_8.toString());
                String domain = "";
                List<ATVNetMiscTableModel> miscs = miscTableService.getList(
                        SharedConstValue.FACTORY_ID,
                        "EMES_DOMAIN",
                        "PRD",
                        "");
                if (!miscs.isEmpty()) {
                    domain = miscs.get(0).getLongDesc();
                }

                domain += "eMES/commons/fileDownloader.do?filePath=" + filePath;
                URL url = new URL(domain);
                BufferedInputStream in = new BufferedInputStream(url.openStream());
                resource = new InputStreamResource(new BufferedInputStream(in));
                note = "Get online schedule sheet memo file api";

                // converting excel to json
                Workbook workbook = new XSSFWorkbook(resource.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);

                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = mapper.createArrayNode();

                Iterator<Row> rowIterator = sheet.iterator();
                Row headerRow = rowIterator.next();
                int colCount = headerRow.getPhysicalNumberOfCells();

                while (rowIterator.hasNext()) {
                    Row currentRow = rowIterator.next();
                    ObjectNode objectNode = mapper.createObjectNode();
                    for (int i = 0; i < colCount; i++) {
                        Cell cell = currentRow.getCell(i);
                        if (cell != null) {
                            String header = headerRow.getCell(i).getStringCellValue();
                            String value = getFormattedCellValue(cell);
                            objectNode.put(header, value);
                        }

                    }
                    arrayNode.add(objectNode);
                }

                // convert json string to result object
                String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
                data = mapper.readValue(jsonString, new TypeReference<List<HashMap<String, Object>>>() {
                });
                msg = "SUCCESS";
                workbook.close();

                // logging
                logging.setCifcid(fileModel.getFactoryID());
                logging.setCiasid(SharedConstValue.SITE_ID);
                logging.setCichdt(currentDateTime);
                logging.setCichbg(Integer.parseInt(userBadge));
                logging.setCiogvl("API_getMemoFile");
                logging.setCirsn("logForAPI");
                this.ITFAService.addApiLogging(logging);
                this.apiLoggingService.insertLog(new ATVNetAPILoggingModel(
                        userBadge,
                        currentDateTime,
                        "{lotName: " + lotName + ", station: " + station + "}",
                        note
                ));
            } else {
                msg = null;
                data = null;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            msg = ex.getMessage();
            data = null;
        }
        return ApiResponse.of(
                data != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST,
                data != null ? ApiResponse.Code.SUCCESS : ApiResponse.Code.FAILED,
                msg,
                data);
    }

    private static String getFormattedCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    DecimalFormat df = new DecimalFormat("#");
                    return df.format(cell.getNumericCellValue());
                }
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            case ERROR:
                return "ERROR";
            default:
                return "";
        }
    }

//    @RequestMapping(method = RequestMethod.POST, value = "/data400/holdLot")
//    @CrossOrigin(origins = "*")
//    public ApiResponse<String> holdLot(@RequestBody HashMap<String, Object> body) {
//        String msg;
//        try {
//            String lotName = body.get("lotName").toString();
//            String lotDcc = body.get("lotDcc").toString();
//            String holdCode = body.get("holdCode").toString();
//            String holdReason = body.get("holdReason").toString();
//            String userBadge = body.get("userBadge").toString();
//            msg = ITFAService.holdLot(lotName, lotDcc, holdCode, holdReason, userBadge);
//            return ApiResponse.of(
//                    HttpStatus.OK,
//                    ApiResponse.Code.SUCCESS,
//                    SUCCESS_MESSAGE,
//                    msg
//            );
//        } catch (Exception ex) {
//            log.error(ex.getMessage());
//            msg = "exception";
//            return ApiResponse.of(
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    ApiResponse.Code.FAILED,
//                    FAILED_MESSAGE,
//                    msg
//            );
//        }
//
//    }


    @RequestMapping(method = RequestMethod.GET, value = "/data400/dateCodeDiscrepancyChecking")
    public ApiResponse<List<DateCodeDiscrepancyModel>> dateCodeDiscrepancyChecking() {
        List<DateCodeDiscrepancyModel> result = new ArrayList<>();
        try {
            result = this.ITFAService.getDateCodeDiscrepancy();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ApiResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiResponse.Code.FAILED,
                    FAILED_MESSAGE,
                    null);
        }
        return ApiResponse.of(
                HttpStatus.OK,
                ApiResponse.Code.SUCCESS,
                SUCCESS_MESSAGE,
                result
        );
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data400/dateCodeDiscrepancyChecking/Y")
    public String sendMailReportDateCodeDiscrepancyChecking(@RequestBody Map<String, Object> body) throws Exception {
        try {
            this.ITFAService.sendMailReportDateCodeDiscrepancyChecking(body);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return FAILED_MESSAGE;
        }
        return SUCCESS_MESSAGE;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data400/releaseLot")
    @CrossOrigin(origins = "*")
    public ApiResponse<String> releaseLot(@RequestBody HashMap<String, Object> body) {
        String msg = "";

        try {
            String lotName = body.get("lotName").toString();
            String lotDcc = body.get("lotDcc").toString();
            String holdCode = body.get("holdCode").toString();
            String releaseReason = body.get("releaseReason").toString();
            String userBadge = body.get("userBadge").toString();
            int holdOpr = (int) body.get("holdOpr");
            Integer shipBackDateInt = (Integer) body.get("shipBackDate");
            long shipBackDate = shipBackDateInt.longValue();
            msg = ITFAService.releaseLot(lotName, lotDcc, holdCode, releaseReason, userBadge, holdOpr, shipBackDate);
            String logBody = "{lotName: " + lotName + ", lotDcc: " + lotDcc + ", holdCode: " + holdCode + ", holdOpr: " + holdOpr + ", releaseReason: " + releaseReason + ", userBadge: " + userBadge + "}";
            // init api logging
            this.apiLoggingService.insertLog(new ATVNetAPILoggingModel(
                    userBadge,
                    this.iatvService.getDateTime(),
                    logBody,
                    "API release lot called by " + userBadge
            ));
            return ApiResponse.of(
                    HttpStatus.OK,
                    ApiResponse.Code.SUCCESS,
                    SUCCESS_MESSAGE,
                    msg
            );
        } catch (Exception ex) {
            log.error(ex.getMessage());
            msg = "exception";
            return ApiResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiResponse.Code.FAILED,
                    FAILED_MESSAGE,
                    msg
            );
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/data400/getDefectListByLotName")
    @CrossOrigin(origins = "*")
    public ApiResponse<String> getDefectListByLotName(@RequestParam("lotName") String lotName) {
        Map<String, Object> result = new HashMap<>();
        String msg = "";
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            Long current = iatvService.getDateTime();
            String trimmedCurrent = String.valueOf(current).substring(4);
            result = ITFAService.getDefectListByLotName(lotName);
            List<String> orderedDefect = Arrays.asList(
                    "Coplanarity",
                    "Edge FM",
                    "Surface contamination",
                    "Matrix quality",
                    "Ball height",
                    "Ball quality",
                    "Side defect",
                    "Surface damage",
                    "Mark",
                    "Edge Straightness",
                    "IR inspection",
                    "Die FM"
            );

            // write result to csv file
            String fileName = "TNR_" + lotName + "_" + trimmedCurrent + ".csv";
            try (FileWriter file = new FileWriter(fileName)) {
                file.write("Lot#,Device,In,Out,Yield,Reject,Coplanarity,Edge FM,Surface contamination,Matrix quality,Ball height,Ball quality,Side defect,Surface damage,Mark,Edge Straightness,IR inspection,Die FM\n");
                StringBuilder lineValue = new StringBuilder();
                int totalRejectQty = Integer.parseInt(result.getOrDefault("TotalRejectQty", 0).toString());
                int totalInQty = Integer.parseInt(result.getOrDefault("WMINQT", 0).toString());
                String device = result.getOrDefault("SSDEVC", "").toString();

                // lot name
                lineValue.append(lotName);
                lineValue.append(",");

                // device
                lineValue.append(device);
                lineValue.append(",");

                // in qty
                lineValue.append(totalInQty);
                lineValue.append(",");

                // out qty
                lineValue.append(totalInQty - totalRejectQty);
                lineValue.append(",");

                // tính yield %
                double yieldPercent = ((double) (totalInQty - totalRejectQty) * 100.0) / totalInQty;
                lineValue.append(String.format("%.2f%%", yieldPercent));

                lineValue.append(",");

                // total reject
                lineValue.append(totalRejectQty);
                lineValue.append(",");

                // defect
                for (int i = 0; i < orderedDefect.size(); i++) {
                    String order = orderedDefect.get(i);
                    List<String> defectName = defectMap.get(order);
                    int totalRejectPerDefectQty = 0;

                    for (String defect : defectName) {
                        totalRejectPerDefectQty += Integer.parseInt(result.getOrDefault(defect, 0).toString());
                    }

                    lineValue.append(totalRejectPerDefectQty);
                    if (i < orderedDefect.size() - 1) {
                        lineValue.append(",");
                    }
                }
                // line break
                lineValue.append("\n");
                file.write(lineValue.toString());
            }

            // upload to FTP
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect("10.201.10.165", 21);
                ftpClient.login("V1QORVOEOL", "Matkhauftp03@");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                File csvFile = new File(fileName);
                boolean done;
                try (FileInputStream inputStream = new FileInputStream(csvFile)) {
                    done = ftpClient.storeFile("/In/WIP/TNR_Data/" + fileName, inputStream);
                }

                if (done) {
                    System.out.println("CSV file uploaded successfully!");
                    // Xóa file local sau khi upload thành công
                    if (csvFile.delete()) {
                        System.out.println("Local file deleted.");
                    } else {
                        System.out.println("Failed to delete local file.");
                    }
                } else {
                    System.out.println("Upload failed.");
                }

                ftpClient.logout();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            msg = "exception";
        }
        return ApiResponse.of(
                HttpStatus.OK,
                ApiResponse.Code.SUCCESS,
                SUCCESS_MESSAGE,
                SUCCESS_MESSAGE
        );
    }

}
