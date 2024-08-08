package com.amkor.controller.v1;


import com.amkor.models.*;
import com.amkor.service.ATVService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
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
        Long dateStart = Long.parseLong(currentDate() + "000000");
        Long dateEnd = Long.parseLong(currentDate() + "230000");
        String result = "Fail";
        List<String> locationList = new ArrayList<>();
        String customer = "( 948,78  )";
        try {
            Class.forName(DRIVER);
            m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));
            String query = "SELECT DISTINCT  DMCSCD, DMLOT#,DMDCC,DMSDEV,DMDAMK,DMEOHQ,DMWEOH,DMRLOC,CICHDT,CICHFD,CIOGVL,CINWVL  FROM  EMLIB.ADSTMP01\n" +
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
                locationList.add(m_rs.getString("DMRLOC").trim());

                if (m_rs.getString("CIOGVL") != null) {
                    lotInformationModel.setResponseMessage(m_rs.getString("CIOGVL").trim());
                    lotInformationModel.setResponseMessageDesc(m_rs.getString("CINWVL").trim());
                }

                dataSearch.add(lotInformationModel);


            }


            m_psmt.close();
            m_rs.close();


            m_conn.close();
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
            query = "SELECT DISTINCT  DMCSCD, DMLOT#,DMDCC,DMSDEV,DMDAMK,DMEOHQ,DMWEOH,DMRLOC,CICHDT,CICHFD,CIOGVL,CINWVL FROM  EMLIB.ADSTMP01\n" +
                    "LEFT JOIN   EMLIB.EMESLP04 ON DMFCID=CIFCID AND DMASID=CIASID AND DMDAMK=CIAMKR AND CICHFD = 'MSCAN' \n" +
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
//        Long dateStart = Long.parseLong("20240806");

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
                    "  WHERE SMFCID=80 AND SMASID=1 AND SMPLNT='V1'  AND SMACDT<>0 AND SMISLF='Y' AND SMSTS1<>'CLOSE' AND SMSTN ='SHIPMENT'  AND SMSTS2 IN " + Status;


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


                listData.add(lotInformationModel);


            }
            listData=getFPO(listData,"TEST");


            m_psmt.close();
            m_rs.close();


            m_conn.close();
            listData=getFPO(listData,"TEST");
            if (listData.size() > 0) {
                List<LotInformationModel> listLotByLocation = new ArrayList<>();

                result = "Send Email Success";
                String fileName = "C:\\Dao\\SendMail\\";

                String fileNameString = Status.trim().equals("('ACTIVE')") ? "ShippingEntry" + currentDate() + ".xls" : "ShippingInventory" + currentDate() + ".xls";
                fileName = fileName + fileNameString;
                createWorkbookShipmentEntry(new File(fileName), listData, fileNameString, Status);
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
            row.createCell(6).setCellValue("Device");
            row.createCell(7).setCellValue("Location");
            row.createCell(8).setCellValue("Scanned");

            int rowCount = 5;


            for (LotInformationModel lot : listLotByLocation) {

                Row lotRow = sheet.createRow(rowCount);

                lotRow.createCell(0).setCellValue(rowCount - 4);
                lotRow.createCell(1).setCellValue(lot.getCustCode());
                lotRow.createCell(2).setCellValue(lot.getCustLot());
                lotRow.createCell(3).setCellValue(lot.getCustDcc());
                lotRow.createCell(4).setCellValue(lot.getEohQty());
                lotRow.createCell(5).setCellValue(lot.getEohWaferQty());
                lotRow.createCell(6).setCellValue(lot.getSourceDevice());
                lotRow.createCell(7).setCellValue(lot.getRackLocationCode());
                lotRow.createCell(8).setCellValue(lot.isScanned() ? "Y" : "N");


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

    public void createWorkbookShipmentEntry(File fileName, ArrayList<LotInformationModel> lotList, String fileNameString, String status) throws IOException {
        try {


            if (fileName.exists()) {
                fileName.delete();
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
            if (lotList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(3, lotList.size() + 2, 0, 0));
            }


            Row row = sheet.createRow(2);
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
            for (int i = 0; i < 13; i++) {
                row.getCell(i).setCellStyle(style);
                sheet.autoSizeColumn(i);
            }

            int rowCount = 3;


            for (LotInformationModel lot : lotList) {

                Row lotRow = sheet.createRow(rowCount);

                lotRow.createCell(0).setCellValue(currentDate());
                lotRow.createCell(1).setCellValue(rowCount - 2);
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
                for (int i = 0; i < 13; i++) {
                    lotRow.getCell(i).setCellStyle(style);
                    sheet.autoSizeColumn(i);
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

    public ArrayList<LotInformationModel> getFPO(ArrayList<LotInformationModel> listLot,String bizType){
        for (LotInformationModel lotInformationModel : listLot) {
            String 	sValue="";
            ResultSet m_rs = null;
            PreparedStatement m_pstmt;
            try
            {
                Class.forName(DRIVER);
                Connection m_conn = DriverManager.getConnection(getURL("ATV"), getUserID("ATV"), getPasswd("ATV"));


                String sQuery="  select CVFLDV from EMLIB.EMESTP02 "
                        + "	 where CVFCID=? and CVBZTP=? and CVASID=? and CVAMKR=? and CVSUB#=?  and CVFLDN=? ORDER BY CVCRDT DESC";
                m_pstmt = m_conn.prepareStatement(sQuery);

                m_pstmt.setInt(1,80);
                m_pstmt.setString(2, bizType);
                m_pstmt.setInt(3,1 );
                m_pstmt.setLong(4,lotInformationModel.getWipAmkorID());
                m_pstmt.setInt(5, lotInformationModel.getWipAmkorSubID());
                m_pstmt.setString(6, "FPO#");
                m_rs=m_pstmt.executeQuery();

                if (m_rs.next())
                {
                    sValue = m_rs.getString("CVFLDV").trim();
                    if(sValue!="") {
                        System.out.println("aa1"+sValue);
                        lotInformationModel.setTraceCode(sValue);
                    }
                }
            }catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

        }

        return listLot;
    }
}
