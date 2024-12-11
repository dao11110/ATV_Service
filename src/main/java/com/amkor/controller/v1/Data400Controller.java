package com.amkor.controller.v1;


import com.amkor.common.response.ApiResponse;
import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.*;
import com.amkor.service.APILoggingService;
import com.amkor.service.ATVNetMiscTableService;
import com.amkor.service.ATVService;
import com.amkor.service.iService.IATVService;
import com.amkor.service.iService.IATVThanhService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class Data400Controller {
    private static String DRIVER = "com.ibm.as400.access.AS400JDBCDriver";
    @Autowired
    private ATVService atvService;

    @Autowired
    private IATVThanhService iatvThanhService;

    @Autowired
    private IATVService iatvService;

    @Autowired
    private APILoggingService apiLoggingService;

    @Autowired
    private ATVNetMiscTableService miscTableService;

    public String getURL(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "jdbc:as400://10.101.6.12";
                break;
            case "ATV":
                result = "jdbc:as400://10.201.6.11";
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
                result = "pruser";
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
                result = "prod0504";
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

    @RequestMapping(method = RequestMethod.GET, value = "/sendMailDieBankInventory")
    public String getDiebankInventoryLotList(
            @RequestParam("cus") int cus) {
        ArrayList<LotInformationModel> dataSearch = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        Long dateStart = Long.parseLong(currentDate()+ "000000");
        Long dateEnd = Long.parseLong(currentDate() + "230000");
        String result = "Fail";
        List<String> locationList = new ArrayList<>();
        String customer = "( 948,78  )";
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            String query = "SELECT DISTINCT  DMCSCD, DMLOT#,DMDCC,DMSDEV,DMDAMK,DMEOHQ,DMWEOH,DMRLOC,CICHDT,CICHFD,CIOGVL,CINWVL,XBATCH,XMTLNO,DMLTCD  FROM  EMLIB.ADSTMP01\n" +
                    "INNER JOIN EMLIB.XREFWFP ON DMFCID=XFCID AND DMASID=XASID AND DMDAMK=XAMKID " +
                    "LEFT JOIN   EMLIB.EMESLP04 ON DMFCID=CIFCID AND DMASID=CIASID AND DMDAMK=CIAMKR AND CICHFD = 'MSCAN' \n" +
                    " WHERE DMFCID=80 AND DMASID=1 AND DCPLNT = 'V1' AND DMSTN = 'DIEBANK' AND DMSTS2 IN ( 'ACTIVE',  'HOLD')" +
                    " AND DMDAMK NOT IN (SELECT DISTINCT (SLAMKR) FROM EMLIB.EMESLP12 WHERE DMFCID=SLFCID AND DMASID=SLASID AND DMDAMK=SLAMKR AND (SLLOCT='L' OR SLLOCT='T' )   )" +
                    " AND  DMCSCD IN " + customer
                    + " AND CICHDT >=" + dateStart + " AND CICHDT <=" + dateEnd + " ORDER  BY DMCSCD ";
            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setCustCode(m_rs.getInt("DMCSCD"));
                lotInformationModel.setCustLot(m_rs.getString("DMLOT#").trim());
                lotInformationModel.setCustDcc(m_rs.getString("DMDCC").trim());
                lotInformationModel.setSourceDevice(m_rs.getString("DMSDEV").trim());
                lotInformationModel.setCustAmkorID(m_rs.getInt("DMDAMK"));
                lotInformationModel.setEohQty(m_rs.getInt("DMEOHQ"));
                lotInformationModel.setEohWaferQty(m_rs.getInt("DMWEOH"));
                lotInformationModel.setRackLocationCode(m_rs.getString("DMRLOC").trim());
//                locationList.add(m_rs.getString("DMRLOC").trim());
                lotInformationModel.setFgsNo(m_rs.getString("XBATCH").trim());
                lotInformationModel.setBinNo(m_rs.getString("XMTLNO").trim());
                lotInformationModel.setLotType(m_rs.getString("DMLTCD").trim());

                if (m_rs.getString("CIOGVL") != null) {
                    lotInformationModel.setResponseMessage(m_rs.getString("CIOGVL").trim());
                    lotInformationModel.setResponseMessageDesc(m_rs.getString("CINWVL").trim());
                }

                dataSearch.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();
            locationList=listLocationDiebank();
            if (dataSearch.size() > 0) {
                List<LotInformationModel> listLotByLocation = new ArrayList<>();
                listLotByLocation = checkLotByLocation(locationList, customer);
                result = "Send Email Success";
                String fileName = "C:\\Dao\\SendMail\\";
                String fileNameString = "Inventory" + currentDate() + ".xls";
                fileName = fileName + fileNameString;
                createWorkbook(new File(fileName), dataSearch, fileNameString, listLotByLocation);
            } else {
                result = "There is no data inventory";
            }


        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
    private List<String>listLocationDiebank(){
        List<String>listLocation=new ArrayList<>();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            String query = "  SELECT DISTINCT DMRLOC  FROM EMLIB.ADSTMP01 ";
            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                listLocation.add(m_rs.getString("DMRLOC").trim());
            }
            m_psmt.close();
            m_rs.close();
            m_conn.close();
        }catch (Exception e) {
            System.out.println(e);
        }
        return listLocation;
    }

    //    @RequestMapping(method = RequestMethod.GET, value = "/getListDieByLocation")
    private List<LotInformationModel> checkLotByLocation(List<String> listLocation, String cus) {
//        List<String>listLocation=new ArrayList<>();
        listLocation.add("A00103");
        listLocation.add("A00101");
        String location = "( ";
        for (String s : listLocation) {
            location += "'" + s + "'" + ",";
        }
        location = location.substring(0, location.length() - 1);
        location = location + " )";
        log.debug("aaa" + location);
        List<LotInformationModel> data = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();

        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        Long dateStart = Long.parseLong(currentDate() + "000000");
        Long dateEnd = Long.parseLong(currentDate() + "230000");
        String result = "Fail";
        String query = "";
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT DISTINCT  DMCSCD, DMLOT#,DMDCC,DMSDEV,DMDAMK,DMEOHQ,DMWEOH,DMRLOC,CICHDT,CICHFD,CIOGVL,CINWVL ,XBATCH,XMTLNO,DMLTCD   FROM  EMLIB.ADSTMP01 " +
                    "INNER JOIN EMLIB.XREFWFP ON DMFCID=XFCID AND DMASID=XASID AND DMDAMK=XAMKID " +
                    "LEFT JOIN   EMLIB.EMESLP04 ON DMFCID=CIFCID AND DMASID=CIASID AND DMDAMK=CIAMKR AND CICHFD = 'MSCAN'  " +
                    " WHERE DMFCID=80 AND DMASID=1 AND DCPLNT = 'V1' AND DMSTN = 'DIEBANK' AND DMSTS2 IN ( 'ACTIVE',  'HOLD')" +
                    " AND DMDAMK NOT IN (SELECT DISTINCT (SLAMKR) FROM EMLIB.EMESLP12 WHERE DMFCID=SLFCID AND DMASID=SLASID AND DMDAMK=SLAMKR  AND (SLLOCT='L' OR SLLOCT='T' )  )" +
                    "  AND DMCSCD  IN " + cus + " AND DMRLOC IN " + location + " ORDER  BY DMCSCD ";
//
            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setCustCode(m_rs.getInt("DMCSCD"));
                lotInformationModel.setCustLot(m_rs.getString("DMLOT#").trim());
                lotInformationModel.setCustDcc(m_rs.getString("DMDCC").trim());
                lotInformationModel.setSourceDevice(m_rs.getString("DMSDEV").trim());
                lotInformationModel.setCustAmkorID(m_rs.getInt("DMDAMK"));
                lotInformationModel.setEohQty(m_rs.getInt("DMEOHQ"));
                lotInformationModel.setEohWaferQty(m_rs.getInt("DMWEOH"));
                lotInformationModel.setRackLocationCode(m_rs.getString("DMRLOC").trim());
                lotInformationModel.setLotType(m_rs.getString("DMLTCD").trim());

                lotInformationModel.setFgsNo(m_rs.getString("XBATCH").trim());
                lotInformationModel.setBinNo(m_rs.getString("XMTLNO").trim());
                if (m_rs.getString("CIOGVL") != null) {
                    lotInformationModel.setResponseMessage(m_rs.getString("CIOGVL").trim());
                    lotInformationModel.setResponseMessageDesc(m_rs.getString("CINWVL").trim());
                }

                data.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();


        } catch (Exception e) {
            System.out.println(e);
        }


        return data;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sendMailShipmentEntry")
    public String sendMailShipmentEntry(@RequestParam(value = "Status") String Status) {
        ArrayList<LotInformationModel> listData = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        Long dateStart = Long.parseLong(currentDate());
//        Long dateStart = Long.parseLong("20240918");

        String result = "Fail";
        String query = "";
        if (Status.trim().equals("ACTIVE")) {
            Status = "('ACTIVE')";
        } else if (Status.trim().equals("WAIT SHIPPING")) {
            Status = "('WAIT SHIPPING')";
        } else {
            Status = "('ACTIVE','HOLD')";
        }
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT * FROM EMLIB.ASCHMP02  " +
                    "  JOIN EMLIB.ASCHMP03 ON SSFCID=SMFCID AND SSASID=SMASID AND SSWAMK=SMWAMK AND SSSUB#=SMSUB#  " +
                    "  AND SSBZTP=CASE WHEN SMBUSN <>'A' THEN 'TEST' ELSE 'ASSY' END  " +
                    "  LEFT JOIN EMLIB.EMESLP04 ON SMFCID=CIFCID AND SMASID=CIASID AND SMWAMK=CIAMKR AND SMSUB#=CISUB# AND CICHFD = 'MSCAN' " +
                    "  WHERE SMFCID=80 AND SMASID=1 AND SMPLNT='V1'  AND SMACDT<>0 AND SMISLF='Y' AND SMSTS1<>'CLOSE' AND SMSTN IN ('SHIPMENT','D-CENTER')  AND SMSTS2 IN " + Status;


            if (Status.trim().equals("('ACTIVE')")) {
                query += "AND SMACDT = " + dateStart;
            } else {
                query += "AND     CINWVL LIKE '" + currentDate() + "%'";
            }
            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setCustCode(m_rs.getInt("SMSCST"));
                lotInformationModel.setCustLot(m_rs.getString("SMLOT#").trim());
                lotInformationModel.setWipAmkorSubID(m_rs.getInt("SSSUB#"));
                lotInformationModel.setCustDcc(m_rs.getString("SMDCC").trim());
                lotInformationModel.setTargetDevice(m_rs.getString("SSDEVC").trim());
                lotInformationModel.setWipAmkorID(m_rs.getInt("SMWAMK"));
                lotInformationModel.setEohQty(m_rs.getInt("SMSIGQ"));
                lotInformationModel.setEohWaferQty(m_rs.getInt("SMEHGQ"));
                lotInformationModel.setReturnQty(m_rs.getInt("SMEOH"));
                lotInformationModel.setBoxNo(m_rs.getString("SMIBOX").trim());
                lotInformationModel.setBadge(m_rs.getInt("SMABG#"));
                lotInformationModel.setRackLocationCode(m_rs.getString("SMSHLF").trim());
                lotInformationModel.setTraceCode(m_rs.getString("SSTRCD").trim());
                lotInformationModel.setScanned(true);
                lotInformationModel.setStatus2(m_rs.getString("SMSTS2").trim());

                listData.add(lotInformationModel);


            }
//            listData=getFPO(listData,"TEST");


            m_psmt.close();
            m_rs.close();


            m_conn.close();
            listData = getFPO(listData, "TEST");
            if (getListShippingInventory().size() > 0) {
                List<LotInformationModel> listLotByLocation = new ArrayList<>();

                result = "Send Email Success";
                String fileName = "C:\\Dao\\SendMail\\";

                String fileNameString = Status.trim().equals("('ACTIVE')") ? "ShippingEntry" + currentDate() + ".xls" : "ShippingInventory" + currentDate() + ".xls";
                fileName = fileName + fileNameString;
                createWorkbookShipmentEntry(new File(fileName), listData, getListShippingInventory(), fileNameString, Status);
            } else {
                result = "There is no data Shipment ";
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    private ArrayList<LotInformationModel> getListShippingInventory() {
        ArrayList<LotInformationModel> data = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        String query;
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT * FROM EMLIB.ASCHMP02  " +
                    "  JOIN EMLIB.ASCHMP03 ON SSFCID=SMFCID AND SSASID=SMASID AND SSWAMK=SMWAMK AND SSSUB#=SMSUB#  " +
                    "  AND SSBZTP=CASE WHEN SMBUSN <>'A' THEN 'TEST' ELSE 'ASSY' END  " +

                    "  WHERE SMFCID=80 AND SMASID=1 AND SMPLNT='V1'  AND SMSTS1<>'CLOSE' AND SMSTN IN ('SHIPMENT','D-CENTER')  AND SMSTS2 IN ('ACTIVE','HOLD')";


            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setCustCode(m_rs.getInt("SMSCST"));
                lotInformationModel.setCustLot(m_rs.getString("SMLOT#").trim());
                lotInformationModel.setWipAmkorSubID(m_rs.getInt("SSSUB#"));
                lotInformationModel.setCustDcc(m_rs.getString("SMDCC").trim());
                lotInformationModel.setTargetDevice(m_rs.getString("SSDEVC").trim());
                lotInformationModel.setWipAmkorID(m_rs.getInt("SMWAMK"));
                lotInformationModel.setEohQty(m_rs.getInt("SMSIGQ"));
                lotInformationModel.setEohWaferQty(m_rs.getInt("SMEHGQ"));
                lotInformationModel.setReturnQty(m_rs.getInt("SMEOH"));
                lotInformationModel.setBoxNo(m_rs.getString("SMIBOX").trim());
                lotInformationModel.setBadge(m_rs.getInt("SMABG#"));
                lotInformationModel.setRackLocationCode(m_rs.getString("SMSHLF").trim());
                lotInformationModel.setTraceCode(m_rs.getString("SSTRCD").trim());
                lotInformationModel.setStatus2(m_rs.getString("SMSTS2").trim());


                data.add(lotInformationModel);


            }
            data = getFPO(data, "TEST");


            m_psmt.close();
            m_rs.close();


            m_conn.close();


        } catch (Exception e) {
            System.out.println(e);
        }
        return data;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sendMailNGInventory")
    public String sendMailNGInventory() {
        ArrayList<LotInformationModel> listData = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        Long dateStart = Long.parseLong(currentDate() + "000000");
        Long dateEnd = Long.parseLong(currentDate() + "235959");

        String result = "Fail";
        String query = "";

        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT A.FACTORY_ID,A.SITE_ID,A.CUSTOMER_NO,A.LOT_NO,A.LOT_DCC, A.AMKOR_ID, A.SUB_ID,A.EOH_QTY,A.OPERATION_NO,A.DEVICE,A.STATUS2,A.RACK_NO,A.SHELF_NO,B.CHANGE_BADGE,C.LOG_REMARK FROM EMLIB.ANGSTP01 AS A " +
                    " JOIN  EMLIB.EMESLP30 AS B ON A.FACTORY_ID = B.FACTORY_ID AND A.SITE_ID = B.SITE_ID AND A.AMKOR_ID = B.AMKOR_ID AND A.SUB_ID =B.SUB_ID AND A.OPERATION_NO = B.SEQUENCE_NO " +
                    "LEFT JOIN  EMLIB.EMESLP30 AS C ON A.FACTORY_ID = C.FACTORY_ID AND A.SITE_ID = C.SITE_ID AND A.AMKOR_ID = C.AMKOR_ID AND A.SUB_ID =C.SUB_ID AND A.OPERATION_NO = C.SEQUENCE_NO AND C.TRNX_MODE = 'BOXID'" +
                    " WHERE B.FACTORY_ID =80 AND B.SITE_ID =1 AND FR_PLANT = 'V1' AND A.STATUS2 IN ('ACTIVE','HOLD')  AND B.TRNX_MODE ='INVENTORY' AND B.LOG_REMARK='CHECKED' AND B.CHANGE_DATETIME BETWEEN " + dateStart + " AND " + dateEnd + "  ORDER BY A.CUSTOMER_NO ";


            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setFactoryID(m_rs.getInt("FACTORY_ID"));
                lotInformationModel.setSiteID(m_rs.getInt("SITE_ID"));
                lotInformationModel.setCustCode(m_rs.getInt("CUSTOMER_NO"));
                lotInformationModel.setWipLot(m_rs.getString("LOT_NO").trim());
                lotInformationModel.setWipDcc(m_rs.getString("LOT_DCC").trim());
                lotInformationModel.setWipAmkorID(m_rs.getInt("AMKOR_ID"));
                lotInformationModel.setWipAmkorSubID(m_rs.getInt("SUB_ID"));
                lotInformationModel.setEohQty(m_rs.getInt("EOH_QTY"));
                lotInformationModel.setOperationNo(m_rs.getInt("OPERATION_NO"));
                lotInformationModel.setTargetDevice(m_rs.getString("DEVICE").trim());
                lotInformationModel.setStatus2(m_rs.getString("STATUS2").trim());
                lotInformationModel.setBadge(Integer.parseInt(m_rs.getString("CHANGE_BADGE").trim()));
                lotInformationModel.setStripMark(m_rs.getString("LOG_REMARK").trim());
                lotInformationModel.setRackLocationCode(m_rs.getString("RACK_NO").trim());
                lotInformationModel.setShelfLocationCode(m_rs.getString("SHELF_NO").trim());

                lotInformationModel.setScanned(true);


                listData.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();


            if (getNGInventory().size() > 0) {
                List<LotInformationModel> listLotByLocation = new ArrayList<>();

                result = "Send Email Success";
                String fileName = "C:\\Dao\\SendMail\\";

                String fileNameString = "NGStoreInventory" + currentDate() + ".xls";
                fileName = fileName + fileNameString;
                createWorkbookNGStoreInventory(new File(fileName), listData, getNGInventory(), fileNameString);
            } else {
                result = "There is no data Shipment ";
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    private ArrayList<LotInformationModel> getNGInventory() {
        ArrayList<LotInformationModel> data = new ArrayList<>();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        String query = "";
        LotInformationModel lotInformationModel = new LotInformationModel();

        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT A.FACTORY_ID,A.SITE_ID,A.CUSTOMER_NO,A.LOT_NO,A.LOT_DCC, A.AMKOR_ID, A.SUB_ID,A.EOH_QTY,A.OPERATION_NO,A.DEVICE,A.STATUS2,A.RACK_NO,A.SHELF_NO FROM EMLIB.ANGSTP01 AS A " +

                    " WHERE A.FACTORY_ID =80 AND A.SITE_ID =1 AND FR_PLANT = 'V1'  AND A.STATUS2 IN ('ACTIVE','HOLD') AND A.STATUS1!='CLOSE'  ORDER BY A.CUSTOMER_NO ";


            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setFactoryID(m_rs.getInt("FACTORY_ID"));
                lotInformationModel.setSiteID(m_rs.getInt("SITE_ID"));
                lotInformationModel.setCustCode(m_rs.getInt("CUSTOMER_NO"));
                lotInformationModel.setWipLot(m_rs.getString("LOT_NO").trim());
                lotInformationModel.setWipDcc(m_rs.getString("LOT_DCC").trim());
                lotInformationModel.setWipAmkorID(m_rs.getInt("AMKOR_ID"));
                lotInformationModel.setWipAmkorSubID(m_rs.getInt("SUB_ID"));
                lotInformationModel.setEohQty(m_rs.getInt("EOH_QTY"));
                lotInformationModel.setOperationNo(m_rs.getInt("OPERATION_NO"));
                lotInformationModel.setTargetDevice(m_rs.getString("DEVICE").trim());
                lotInformationModel.setStatus2(m_rs.getString("STATUS2").trim());
                lotInformationModel.setRackLocationCode(m_rs.getString("RACK_NO").trim());
                lotInformationModel.setShelfLocationCode(m_rs.getString("SHELF_NO").trim());


                data.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return data;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sendMailNGScrap")
    public String sendMailNGScrap() {
        ArrayList<LotInformationModel> listData = new ArrayList<>();
        LotInformationModel lotInformationModel = new LotInformationModel();
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        Long dateStart = Long.parseLong(currentDate() + "000000");
        Long dateEnd = Long.parseLong(currentDate() + "235959");

        String result = "Fail";
        String query = "";

        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query = "SELECT NMFCID,NMASID,NMWAMK,NMSUB#,NMOPR,NMSTN,NMSTS1,NMSTS2,NMLOT#,NMDCC,NFPLNT,NTPLNT,NMCSCD,NMPKG,NMDMS,NMLEAD,NMDEVC, NMRCVQ,NMEOH,NMRACK,NMSHLF,NMRQDT,NMRQBG,NMCRDT,NMCRBG,NMSCDT,NRFCID,NRASID,NRWAMK,NRSUB#,NRSEQ,NRMODE,NRSTS,NRBINO,NRQTY, NRREMK,NRMAIL,NRRTND,NRISLC,NRRTNF,NRCRDT,NRCRBG,NRCHDT,NRCHBG , NSBINO \n" +
                    "FROM EMLIB.ANGSTP01 " +
                    "LEFT JOIN EMLIB.ANGSTP02 ON NMFCID=NSFCID AND NMASID=NSASID AND NMWAMK=NSWAMK AND NMSUB#= NSSUB#  AND NMOPR= NSOPR  " +
                    "LEFT JOIN EMLIB.EMESLP30 ON NMFCID=NRFCID AND NMASID= NRASID AND NMWAMK=NRWAMK AND  NMSUB#=NRSUB# AND  NMOPR=NRSEQ  " +
                    "WHERE  NMSTS1='CLOSE' AND NMSTS2='SCRAP' AND  NMEOH=0 AND NRMODE='SCRAP' AND NRSTS='P-SCRAPED' AND NRCRDT >= " + dateStart + " AND NRCRDT <= " + dateEnd;


            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                lotInformationModel = new LotInformationModel();
                lotInformationModel.setFactoryID(m_rs.getInt("NMFCID"));
                lotInformationModel.setSiteID(m_rs.getInt("NMASID"));
                lotInformationModel.setFromPlant(m_rs.getString("NFPLNT").trim());
                lotInformationModel.setCustCode(m_rs.getInt("NMCSCD"));
                lotInformationModel.setWipLot(m_rs.getString("NMLOT#").trim());
                lotInformationModel.setWipDcc(m_rs.getString("NMDCC").trim());
                lotInformationModel.setWipAmkorID(m_rs.getInt("NMWAMK"));
                lotInformationModel.setWipAmkorSubID(m_rs.getInt("NMSUB#"));
                lotInformationModel.setEohQty(m_rs.getInt("NRQTY"));
                lotInformationModel.setOperationNo(m_rs.getInt("NMOPR"));
                lotInformationModel.setTargetDevice(m_rs.getString("NMDEVC").trim());
                lotInformationModel.setStatus2(m_rs.getString("NMSTS2").trim());
                lotInformationModel.setBadge(Integer.parseInt(m_rs.getString("NRCHBG").trim()));
                lotInformationModel.setStripMark(m_rs.getString("NRREMK").trim());
                lotInformationModel.setRackLocationCode(m_rs.getString("NMRACK").trim());
                lotInformationModel.setShelfLocationCode(m_rs.getString("NMSHLF").trim());

                listData.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();

            if (listData.size() > 0) {
                List<LotInformationModel> listLotByLocation = new ArrayList<>();

                result = "Send Email Success";
                String fileName = "C:\\Dao\\SendMail\\";

                String fileNameString = "NGStoreScrap" + currentDate() + ".xls";
                fileName = fileName + fileNameString;
                createWorkbookNGStoreScrap(new File(fileName), listData, fileNameString);
            } else {
                result = "There is no data Shipment ";
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sync/checkWindowTimeHold")
//    @Scheduled(fixedDelay = 1800000, initialDelay = 1800000)
    public List<WindowTimeHoldModel> checkWindowTimeHold() {
        List<WindowTimeHoldModel> listResult = new ArrayList<>();
        List<WindowTimeHoldModel> listData = new ArrayList<>();
        WindowTimeHoldModel windowTimeHoldModel;
        Connection m_conn = null;
        PreparedStatement m_psmt = null;
        CallableStatement m_cs = null;
        ResultSet m_rs = null;
        String result = "Fail";
        String query = "";
        Map<String, List<WindowTimeHoldModel>> mapData = new HashMap<>();
        List<WindowTimeHoldModel> listDataKioxia = new ArrayList<>();
        List<WindowTimeHoldModel> listESI = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            query =
                    "  WITH pb (SMFCID,SMASID,SMWAMK,SMSUB#,SMLOT#,SMDCC ,SMCSCD,SMPKG,SMDMS,SMLEAD,WMINDT,WMOUDT) as " +
                            "  (SELECT SMFCID,SMASID,SMWAMK,SMSUB#,SMLOT#,SMDCC ,SMCSCD,SMPKG,SMDMS,SMLEAD,WMINDT,WMOUDT " +
                            "  FROM EMLIB.ASCHMP02 a " +
                            "   INNER JOIN EMLIB.AWIPMP01 b ON  a.SMFCID =b.WMFCID AND a.SMASID =b.WMASID  AND a.SMSUB# =b.WMSUB#  AND a.SMWAMK =b.WMWAMK " +
                            "  INNER JOIN EMLIB.EMESWINT c ON  a.SMPKG=c.PACKAGE_TYPE AND a.SMDMS=c.DMS AND a.SMLEAD=c.LEAD AND  FROM_OPER_CODE =861 AND TO_OPER_CODE =862 AND PRE_ALERT ='Y' " +
                            "   WHERE SMFCID=80 AND SMASID=1 AND b.WMOPR# =861 AND a.SMSTN ='WIP' AND SMSTS2='ACTIVE' AND SMCSCD IN (78,220,2277) AND b.WMSTS2='INACTIVE')" +

                            "  SELECT SMFCID,SMASID,SMWAMK,SMSUB#,SMLOT#,SMDCC,SMCSCD ,SMPKG,SMDMS,SMLEAD,pb.WMINDT,pb.WMOUDT" +
                            "  FROM   pb " +
                            "  INNER JOIN EMLIB.AWIPMP01 c ON pb.SMFCID =c.WMFCID AND pb.SMASID =c.WMASID  AND pb.SMSUB# =c.WMSUB#  AND pb.SMWAMK =c.WMWAMK  " +
                            "  WHERE   c.WMOPR# =862  AND c.WMSTS2='ACTIVE' ";
//
            m_psmt = m_conn.prepareStatement(query);

            m_rs = m_psmt.executeQuery();
            while (m_rs != null && m_rs.next()) {
                windowTimeHoldModel = new WindowTimeHoldModel();
                windowTimeHoldModel.setNFid(m_rs.getInt("SMFCID"));
                windowTimeHoldModel.setNSid(m_rs.getInt("SMASID"));
                windowTimeHoldModel.setNSub(m_rs.getInt("SMSUB#"));
                windowTimeHoldModel.setWaferAmKorID(m_rs.getLong("SMWAMK"));
                windowTimeHoldModel.setSLot(m_rs.getString("SMLOT#").trim());
                windowTimeHoldModel.setSDcc(m_rs.getString("SMDCC").trim());
                windowTimeHoldModel.setSPkg(m_rs.getString("SMPKG").trim());
                windowTimeHoldModel.setSLead(m_rs.getString("SMLEAD").trim());
                windowTimeHoldModel.setSDms(m_rs.getString("SMDMS").trim());
                windowTimeHoldModel.setInDate(m_rs.getLong("WMINDT"));
                windowTimeHoldModel.setOutDate(m_rs.getLong("WMOUDT"));
                windowTimeHoldModel.setCusCode(m_rs.getInt("SMCSCD"));


                listData.add(windowTimeHoldModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();


            mapData = listData.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getCusCode())));

            // List<WindowTimeHoldModel>listESI=new ArrayList<>();
            if (mapData.get("78") != null) {
                listDataKioxia.addAll(mapData.get("78"));
            }
            if (mapData.get("220") != null) {
                listESI.addAll(mapData.get("220"));
            }
            if (mapData.get("2277") != null) {
                listESI.addAll(mapData.get("2277"));
            }

//            if (listDataKioxia.size() > 0) {
//                sendMaiWindowTimeOut(listDataKioxia, 57600000, 1);
//            }
//            if (listESI.size() > 0) {
//                sendMaiWindowTimeOut(listDataKioxia, 72000000, 2);
//            }


        } catch (Exception e) {
            System.out.println(e);
        }

        return listData;
    }

    private void sendMaiWindowTimeOut(List<WindowTimeHoldModel> listData, long timeWindowOut, int type) {
        List<WindowTimeHoldModel> listResult = new ArrayList<>();

        List<String> listTo = new ArrayList<>();
        listTo.add("Dao.NguyenVan@amkor.com");
        listTo.add("Anh.LeTuan@amkor.com");
//            listTo.add("ATVTESTMEMORY@amkor.com");
//            listTo.add("ATVTESTMFG@amkor.com");
        List<String> listCC = new ArrayList<>();

        for (WindowTimeHoldModel w : listData) {
            String timeOut = String.valueOf(w.getOutDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            try {
                date = sdf.parse(timeOut);
                long msOut = date.getTime();

                Calendar cal = Calendar.getInstance();
                long timeNow = cal.getTimeInMillis();
                if (timeNow - msOut > timeWindowOut) {
                    listResult.add(w);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


        }

        String titleMail = "WinDow Time Pre-Alarm";
        String contentMail = "Dear Team, </br> ";
        contentMail += " Here is Lot# list have time TrackOut in Opr# 861 exceeded 16 hour.So please check. </br>";


        contentMail += "<table align='left' style='border-collapse: collapse' cellspacing='0' width='800'>" +
                "<tr><b>" +
                "<td width='4%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20' bgcolor='E9E6F6'>STT </td>" +
                "<td width='9%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20' bgcolor='E9E6F6'>Lot# </td>" +
                "<td width='4%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20' bgcolor='E9E6F6'>DCC </td>" +
                "<td width='9%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20' bgcolor='E9E6F6'> TrackOut </td>";
        int count = 1;
        for (WindowTimeHoldModel w : listResult) {

            String timeOut = String.valueOf(w.getOutDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            try {
                date = sdf.parse(timeOut);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            sdf.applyPattern("yyyy/MM/dd hh:mm:ss");
            String newOutDate = sdf.format(date);


            contentMail += "<tr><b>" +
                    "<td width='5%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20'> " + count + " </td>" +
                    "<td width='20%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20'>" + w.getSLot() + " </td>" +
                    "<td width='10%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20'>" + w.getSDcc() + "</td>" +
                    "<td width='25%'style='border-width: 1; border-color: #c0c0c0; border-style: solid' align='center' height='20'>" + newOutDate + "</td>" +
                    "</b></tr>";
            count++;
        }
        contentMail += "</table>" +
                "</td>" +
                "</b></tr>" +
                "</table> </br>";

        try {
            atvService.sendMailProcess2(titleMail, listTo, listCC, new ArrayList<>(), contentMail, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void createWorkbook(File fileName, ArrayList<LotInformationModel> lotList, String fileNameString, List<LotInformationModel> listLotByLocation) throws IOException {
        try {


            if (fileName.exists()) {
                fileName.delete();
            }
            for (LotInformationModel lot : listLotByLocation) {
//
                boolean check = lotList.stream().anyMatch(e -> e.getCustLot().equals(lot.getCustLot()) && e.getCustDcc().equals(lot.getCustDcc()));
                if (check) {
                    lot.setScanned(true);
                }

            }

            FileOutputStream fos = new FileOutputStream(fileName);
            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("Inventory");
            CellStyle style = workbook.createCellStyle();

            Row rowLotScanned = sheet.createRow(0);
            rowLotScanned.createCell(0).setCellValue("Scanned Lot: ");


            rowLotScanned.createCell(3).setCellValue(lotList.size() + " / " + listLotByLocation.size());

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));


            Row rowLotNotScanned = sheet.createRow(1);

            rowLotNotScanned.createCell(0).setCellValue("Not Scanned Lot: ");

            rowLotNotScanned.createCell(3).setCellValue(listLotByLocation.size() - lotList.size() + " / " + listLotByLocation.size());
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));


            Row row = sheet.createRow(4);
            row.createCell(0).setCellValue("STT");
            row.createCell(1).setCellValue("Cust#");
            row.createCell(2).setCellValue("Lot#");
            row.createCell(3).setCellValue("DCC");
            row.createCell(4).setCellValue("Die Qty");
            row.createCell(5).setCellValue("Wafer Qty");
            row.createCell(6).setCellValue("Material No");
            row.createCell(7).setCellValue("Batch No");
            row.createCell(8).setCellValue("Lot Type");

            row.createCell(9).setCellValue("Device");
            row.createCell(10).setCellValue("Location");
            row.createCell(11).setCellValue("Scanned");

            int rowCount = 5;


            for (LotInformationModel lot : listLotByLocation) {

                Row lotRow = sheet.createRow(rowCount);

                lotRow.createCell(0).setCellValue(rowCount - 4);
                lotRow.createCell(1).setCellValue(lot.getCustCode());
                lotRow.createCell(2).setCellValue(lot.getCustLot());
                lotRow.createCell(3).setCellValue(lot.getCustDcc());
                lotRow.createCell(4).setCellValue(lot.getEohQty());
                lotRow.createCell(5).setCellValue(lot.getEohWaferQty());
                lotRow.createCell(6).setCellValue(lot.getBinNo());
                lotRow.createCell(7).setCellValue(lot.getFgsNo());
                lotRow.createCell(8).setCellValue(lot.getLotType());
                lotRow.createCell(9).setCellValue(lot.getSourceDevice());
                lotRow.createCell(10).setCellValue(lot.getRackLocationCode());
                lotRow.createCell(11).setCellValue(lot.isScanned() ? "Y" : "N");


                rowCount++;
            }


            workbook.write(fos);
            fos.flush();
            fos.close();
            atvService.sendMailDaily(fileName.getPath(), fileNameString, "Diebank Inventory Daily");
//            for (LotInformationModel lot : listLotByLocation) {
//                System.out.println("aaaa" + lot.isScanned());
//            }


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createWorkbookShipmentEntry(File fileName, ArrayList<LotInformationModel> lotListScanned, ArrayList<LotInformationModel> lotListInventory, String fileNameString, String status) throws IOException {
        try {


            if (fileName.exists()) {
                fileName.delete();
            }
            int nNotScanned = 0;

            if (!status.trim().equals("('ACTIVE')")) {
                for (LotInformationModel lot : lotListInventory) {
                    boolean check = lotListScanned.stream().anyMatch(e -> e.getWipAmkorID() == lot.getWipAmkorID() && e.getWipDcc() == lot.getWipDcc() && e.getWipAmkorSubID() == lot.getWipAmkorSubID());
                    if (!check) {
                        lot.setScanned(false);
                        lotListScanned.add(lot);
                        nNotScanned++;
                    }
                }
            }

            FileOutputStream fos = new FileOutputStream(fileName);
            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("Shipping");
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.BLUE.getIndex());
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            if (lotListScanned.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(3, lotListScanned.size() + 2, 0, 0));
            }


            Row rowLotScanned = sheet.createRow(0);
            rowLotScanned.createCell(0).setCellValue("Scanned Lot: ");


            rowLotScanned.createCell(3).setCellValue(lotListScanned.size() - nNotScanned + " / " + lotListScanned.size());

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));


            Row rowLotNotScanned = sheet.createRow(1);

            rowLotNotScanned.createCell(0).setCellValue("Not Scanned Lot: ");

            rowLotNotScanned.createCell(3).setCellValue(nNotScanned + " / " + lotListScanned.size());
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));


            Row row = sheet.createRow(4);
            row.createCell(0).setCellValue("CheckedDate");
            row.createCell(1).setCellValue("No");
            row.createCell(2).setCellValue("Cust");
            row.createCell(3).setCellValue("Lot#");
            row.createCell(4).setCellValue("DCC");
            row.createCell(5).setCellValue("Trace Code");
            row.createCell(6).setCellValue("Lot Qty");
            row.createCell(7).setCellValue("Scanned Lot Qty");
            row.createCell(8).setCellValue("Gap Lot Qty");
            row.createCell(9).setCellValue("Box Qty");

            row.createCell(10).setCellValue("Target Device");
            row.createCell(11).setCellValue("Location");
            row.createCell(12).setCellValue("Checked User");
            row.createCell(13).setCellValue("Status");
            if (!status.trim().equals("('ACTIVE')")) {
                row.createCell(14).setCellValue("Scanned");
                for (int i = 0; i < 15; i++) {
                    row.getCell(i).setCellStyle(style);
                    sheet.autoSizeColumn(i);
                }
            } else {
                for (int i = 0; i < 14; i++) {
                    row.getCell(i).setCellStyle(style);
                    sheet.autoSizeColumn(i);
                }
            }

            int rowCount = 5;


            for (LotInformationModel lot : lotListScanned) {

                Row lotRow = sheet.createRow(rowCount);

                lotRow.createCell(0).setCellValue(currentDate());
                lotRow.createCell(1).setCellValue(rowCount - 4);
                lotRow.createCell(2).setCellValue(lot.getCustCode());
                lotRow.createCell(3).setCellValue(lot.getCustLot());
                lotRow.createCell(4).setCellValue(lot.getCustDcc());
                lotRow.createCell(5).setCellValue(lot.getTraceCode());
                lotRow.createCell(6).setCellValue(lot.getEohQty());
                lotRow.createCell(7).setCellValue(lot.getEohWaferQty());
                lotRow.createCell(8).setCellValue(lot.getReturnQty());
                lotRow.createCell(9).setCellValue(lot.getBoxNo());
                lotRow.createCell(10).setCellValue(lot.getTargetDevice());
                lotRow.createCell(11).setCellValue(lot.getRackLocationCode());
                lotRow.createCell(12).setCellValue(lot.getBadge());
                lotRow.createCell(13).setCellValue(lot.getStatus2());
                if (!status.trim().equals("('ACTIVE')")) {

                    lotRow.createCell(14).setCellValue(lot.isScanned() ? "Y" : "N");
                    for (int i = 0; i < 15; i++) {
                        lotRow.getCell(i).setCellStyle(style);
                        sheet.autoSizeColumn(i);
                    }
                } else {
                    for (int i = 0; i < 14; i++) {
                        lotRow.getCell(i).setCellStyle(style);
                        sheet.autoSizeColumn(i);
                    }
                }


                rowCount++;
            }
//            for (int i = 0; i < lotList.size() + 2; i++) {
//                Row rowBorder = sheet.getRow(i);
//
//                Cell cellBorder0 = rowBorder.getCell(0);
//                cellBorder0.setCellStyle(style);
//                Cell cellBorder1 = rowBorder.getCell(1);
//                cellBorder1.setCellStyle(style);
//
//
//            }


            workbook.write(fos);
            fos.flush();
            fos.close();
            atvService.sendMailDaily(fileName.getPath(), fileNameString, status.trim().equals("('ACTIVE')") ? "Shipping Entry Daily " : "Shipping Inventory Daily ");
//            for (LotInformationModel lot : listLotByLocation) {
//                System.out.println("aaaa" + lot.isScanned());
//            }


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createWorkbookNGStoreInventory(File fileName, ArrayList<LotInformationModel> lotListChecked, ArrayList<LotInformationModel> listDataInventory, String fileNameString) {
        try {


            if (fileName.exists()) {
                fileName.delete();
            }
            int nNotScanned = 0;
            for (LotInformationModel lot : listDataInventory) {
//
                boolean check = lotListChecked.stream().anyMatch(e -> e.getWipLot().equals(lot.getWipLot()) && e.getWipDcc().equals(lot.getWipDcc()) && lot.getOperationNo() == e.getOperationNo() && e.getWipAmkorSubID() == lot.getWipAmkorSubID());
                if (!check) {
                    lot.setScanned(false);
                    lotListChecked.add(lot);
                    nNotScanned++;
                }

            }


            FileOutputStream fos = new FileOutputStream(fileName);
            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("NG Inventory");
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.BLUE.getIndex());
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
//            if (lotList.size() > 1) {
//                sheet.addMergedRegion(new CellRangeAddress(3, lotList.size() + 2, 0, 0));
//            }

            Row rowLotScanned = sheet.createRow(0);
            rowLotScanned.createCell(0).setCellValue("Scanned Lot: ");


            rowLotScanned.createCell(3).setCellValue(lotListChecked.size() - nNotScanned + " / " + lotListChecked.size());

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));


            Row rowLotNotScanned = sheet.createRow(1);

            rowLotNotScanned.createCell(0).setCellValue("Not Scanned Lot: ");

            rowLotNotScanned.createCell(3).setCellValue(nNotScanned + " / " + lotListChecked.size());
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));


            Row row = sheet.createRow(4);

            row.createCell(0).setCellValue("No");
            row.createCell(1).setCellValue("FGS");
            row.createCell(2).setCellValue("Cust");
            row.createCell(3).setCellValue("Lot#");
            row.createCell(4).setCellValue("DCC");
            row.createCell(5).setCellValue("OPR");
            row.createCell(6).setCellValue("EOH");
            row.createCell(7).setCellValue("TargetDevice");
            row.createCell(8).setCellValue("Status");
            row.createCell(9).setCellValue("CheckedUser");
            row.createCell(10).setCellValue("Location");
            row.createCell(11).setCellValue("Scanned");

            for (int i = 0; i < 12; i++) {
                row.getCell(i).setCellStyle(style);
                sheet.autoSizeColumn(i);
            }

            int rowCount = 5;


            for (LotInformationModel lot : lotListChecked) {

                Row lotRow = sheet.createRow(rowCount);


                lotRow.createCell(0).setCellValue(rowCount - 4);
                lotRow.createCell(1).setCellValue(lot.getStripMark());
                lotRow.createCell(2).setCellValue(lot.getCustCode());
                lotRow.createCell(3).setCellValue(lot.getWipLot());
                lotRow.createCell(4).setCellValue(lot.getWipDcc());
                lotRow.createCell(5).setCellValue(lot.getOperationNo());
                lotRow.createCell(6).setCellValue(lot.getEohQty());
                lotRow.createCell(7).setCellValue(lot.getTargetDevice());
                lotRow.createCell(8).setCellValue(lot.getStatus2());
                lotRow.createCell(9).setCellValue(lot.getBadge());
                lotRow.createCell(10).setCellValue(lot.getRackLocationCode() + "-" + lot.getShelfLocationCode());
                lotRow.createCell(11).setCellValue(lot.isScanned() ? "Y" : "N");


                for (int i = 0; i < 12; i++) {
                    lotRow.getCell(i).setCellStyle(style);
                    sheet.autoSizeColumn(i);
                }

                rowCount++;
            }


            workbook.write(fos);
            fos.flush();
            fos.close();
            atvService.sendMailDaily(fileName.getPath(), fileNameString, "NG Store Inventory Daily");


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createWorkbookNGStoreScrap(File fileName, ArrayList<LotInformationModel> lotList, String fileNameString) {
        try {


            if (fileName.exists()) {
                fileName.delete();
            }


            FileOutputStream fos = new FileOutputStream(fileName);
            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("Scrap");
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.BLUE.getIndex());
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
//            if (lotList.size() > 1) {
//                sheet.addMergedRegion(new CellRangeAddress(3, lotList.size() + 2, 0, 0));
//            }


            Row row = sheet.createRow(2);

            row.createCell(0).setCellValue("No");
            row.createCell(1).setCellValue("Scrap Date");
            row.createCell(2).setCellValue("Plant");
            row.createCell(3).setCellValue("Cust");
            row.createCell(4).setCellValue("Lot#");
            row.createCell(5).setCellValue("Dcc");
            row.createCell(6).setCellValue("Oper#");
            row.createCell(7).setCellValue("EOH");
            row.createCell(8).setCellValue("Rack");
            row.createCell(9).setCellValue("Shelf");
            row.createCell(10).setCellValue("Status");
            row.createCell(11).setCellValue("ScrapUser");

            for (int i = 0; i < 12; i++) {
                row.getCell(i).setCellStyle(style);
                sheet.autoSizeColumn(i);
            }

            int rowCount = 3;


            for (LotInformationModel lot : lotList) {

                Row lotRow = sheet.createRow(rowCount);


                lotRow.createCell(0).setCellValue(rowCount - 2);
                lotRow.createCell(1).setCellValue(currentDate());
                lotRow.createCell(2).setCellValue(lot.getFromPlant());
                lotRow.createCell(3).setCellValue(lot.getCustCode());
                lotRow.createCell(4).setCellValue(lot.getWipLot());
                lotRow.createCell(5).setCellValue(lot.getWipDcc());
                lotRow.createCell(6).setCellValue(lot.getOperationNo());
                lotRow.createCell(7).setCellValue(lot.getEohQty());
                lotRow.createCell(8).setCellValue(lot.getRackLocationCode());
                lotRow.createCell(9).setCellValue(lot.getShelfLocationCode());
                lotRow.createCell(10).setCellValue(lot.getStatus2());
                lotRow.createCell(11).setCellValue(lot.getBadge());

                for (int i = 0; i < 12; i++) {
                    lotRow.getCell(i).setCellStyle(style);
                    sheet.autoSizeColumn(i);
                }

                rowCount++;
            }


            workbook.write(fos);
            fos.flush();
            fos.close();
            atvService.sendMailDaily(fileName.getPath(), fileNameString, "NG Store Scrap Daily");


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String currentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);
        return currentDate;
    }

    public long get400CurrentDate() {
        String current = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long result = Long.parseLong(current) - 19000000000000L;
        return result;
    }

    public ArrayList<LotInformationModel> getFPO(ArrayList<LotInformationModel> listLot, String bizType) {
        for (LotInformationModel lotInformationModel : listLot) {
            String sValue = "";
            ResultSet m_rs = null;
            PreparedStatement m_pstmt;
            try {
                Class.forName(DRIVER);
                Connection m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));


                String sQuery = "  select CVFLDV from EMLIB.EMESTP02 "
                        + "	 where CVFCID=? and CVBZTP=? and CVASID=? and CVAMKR=? and CVSUB#=?  and CVFLDN=? ORDER BY CVCRDT DESC";
                m_pstmt = m_conn.prepareStatement(sQuery);

                m_pstmt.setInt(1, 80);
                m_pstmt.setString(2, bizType);
                m_pstmt.setInt(3, 1);
                m_pstmt.setLong(4, lotInformationModel.getWipAmkorID());
                m_pstmt.setInt(5, lotInformationModel.getWipAmkorSubID());
                m_pstmt.setString(6, "FPO#");
                m_rs = m_pstmt.executeQuery();

                if (m_rs.next()) {
                    if (m_rs.getString("CVFLDV") != null)
                        sValue = m_rs.getString("CVFLDV").trim();
                    if (!sValue.equals("")) {
//                        System.out.println("aa1" + sValue);
                        lotInformationModel.setTraceCode(sValue);
                    }
                }

                m_rs.close();
                m_pstmt.close();
                m_conn.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        return listLot;
    }

    @RequestMapping(method = RequestMethod.GET, value = "labelValidation")
    public String labelValidation(@RequestParam(value = "jsonBody") String jsonBody) {
        String result = "";
        if (jsonBody != null) {
            result = "OK";
        } else
            result = "Not Success";
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
            OnLineScheduleSheetFileModel fileModel = iatvService.getOnlineScheduleSheetMemoFileFromStationAndLotName(lotName, station);
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
                this.iatvThanhService.addApiLogging(logging);
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
}
