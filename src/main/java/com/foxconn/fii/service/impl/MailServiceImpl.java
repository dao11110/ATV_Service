package com.foxconn.fii.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.DataStatic;
import com.foxconn.fii.request.MailMessage;
import com.foxconn.fii.request.hr.UserCovid;
import com.foxconn.fii.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void sendMail(String listUser, String title, String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("title",title);
        tmpMap.put("body", content);

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("system", "MAIL");
        bodyMap.put("type", "TEXT");
        bodyMap.put("source", "Paperless");
        bodyMap.put("from", "");
        bodyMap.put("toUser", listUser);
        bodyMap.put("toGroup", null);
        bodyMap.put("message", objectMapper.writeValueAsString(tmpMap));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyMap, headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(DataStatic.MAIL.MAIL_SERVER, HttpMethod.POST, entity, String.class);
            System.out.println("Send mail: "+responseEntity.getStatusCode().toString());
        } catch (RestClientException e) {
            log.error("### uploadFile error ", e);
            //return false;
        }
    }

    @Override
    public void sendMailAndFile(List<UserCovid> mData, String subContent) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        HSSFWorkbook workbook = new HSSFWorkbook();


        String fileDir = System.getProperty("user.dir").toString() + "\\tempotarydownloaddir\\";
        String[] arrTitle  = {
                "STT"
                ,"Mã thẻ"
                ,"Họ tên (VN)"
                ,"Họ tên (CH)"
                ,"BU"
                ,"CFT"
                ,"Bộ phận"
                ,"Mã bộ phận"
                ,"Tên Bộ phận HT"
        };

        String fileName =(new Random()).nextInt(100)+ " -HRPM- " + simpleDateFormat.format(new Date()) + ".xls";

        HSSFCellStyle titleStyle = createStyle(workbook);
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        HSSFCellStyle normalStyle = createStyle(workbook);

        HSSFCellStyle abnormalStyle = createStyle(workbook);
        HSSFFont abnormalFont = workbook.createFont();
        abnormalFont.setFontName("Times New Roman");
        abnormalFont.setColor(IndexedColors.RED.getIndex());
        abnormalStyle.setFont(abnormalFont);

        HSSFCellStyle restDayStyle = createStyle(workbook);
        restDayStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        restDayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        String dd = "PaperlessHR- "+ simpleDateFormat.format(new Date());
        HSSFSheet sheet = workbook.createSheet(dd);
        int max = 0;
        int rowNum = 0;
        Row row;
        Cell cell;
        int stt = 0;

        row = sheet.createRow(rowNum);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < arrTitle.length; i++){
            cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(arrTitle[i]);
            cell.setCellStyle(titleStyle);

        }
        rowNum++;
        for (int i = 0; i < mData.size(); i++) {
            stt++;
            int cellNum = 0;
            row = sheet.createRow(rowNum);

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(i);
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getEmpId());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getEmpNameVn());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getEmpNameCn());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getBu());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getCft());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getDeptDesc());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getDeptCode());
            cell.setCellStyle(normalStyle);
            cellNum++;

            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(mData.get(i).getDeptName());
            cell.setCellStyle(normalStyle);
            cellNum++;
            rowNum++;

            if (cellNum > max){
                max = cellNum;
            }
        }
        for (int i = 0; i < max; i++) {
            sheet.autoSizeColumn(i);
        }

        File file = new File(fileDir + fileName);
        file.getParentFile().mkdirs();

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        outFile.close();
        // Content-Disposition
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));

        byte[] encoded = Base64.encodeBase64(FileUtil.readAsByteArray(file));
        String nameFile = "PAPERLESS-(Temperature DAILY)-"+simpleDateFormat.format(new Date())+".xls";
        MailMessage message = MailMessage.of("[Paperless-Covid] List user don't have data temperature in Paperless System "+simpleDateFormat.format(new Date())+" Thanks!.", subContent+DataStatic.MAIL.FOOT_MAIL, new String(encoded, StandardCharsets.US_ASCII), nameFile);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(message);
        String mail = "crc-it-vn@mail.foxconn.com,cnsbg-vn-ehs-assistant@mail.foxconn.com,cpe-vn-cost-cpd@mail.foxconn.com,rou.sy.yang@mail.foxconn.com,vn-stc-assistant@mail.foxconn.com,fangfang.sf.li@mail.foxconn.com,joyce.sr.ruan@mail.foxconn.com,oanh.sy.ruan@mail.foxconn.com,oanh.sy.ruan@mail.foxconn.com,cpei-pe-test@mail.foxconn.com,cpe-vn-pc-all@mail.foxconn.com,herbal.qc.wang@mail.foxconn.com,cpeii-vn-pmc-assistant@mail.foxconn.com,cpeii-vn-pmc-assistant@mail.foxconn.com,bill.ws.phan@mail.foxconn.com,lita.xm.li@mail.foxconn.com,eira.sq.ruan@mail.foxconn.com,lynn.sx.fan@mail.foxconn.com,Ruby B.Y. Nguyen/NSG/FOXCONN@FOXCONN,Mike J.S. Kao/CEN/FOXCONN,frank.wl.nguyen@mail.foxconn.com,cmb-vn-mfg-assistant@mail.foxconn.com,cmb-vn-mfg@mail.foxconn.com,cpe-vngw-b04-safety@mail.foxconn.com,huu-loi.tran@mail.foxconn.com,cnsbg-vn-ehs-audit-fm@mail.foxconn.com,cnsbg-vn-ehs-audit-ht@mail.foxconn.com,nsd-vn-fg-warehouse@mail.foxconn.com,cpe-vn-ipqc@mail.foxconn.com,ana.sy.ruan@mail.foxconn.com,emi.sc.wei@mail.foxconn.com,phuong.dung@mail.foxconn.com,ruby.by.nguyen@fii-foxconn.com,mike.js.kao@fii-foxconn.com";
//        String mail = "cpe-vn-fii-app@mail.foxconn.com";
        mail += ",huy-dat.mei@fii-foxconn.com,shi-xing.deng@mail.foxconn.com,amber.wl.tang@foxconn.com,manh-luu.nguyen@fii-foxconn.com,brock.liao@fii-foxconn.com,wen-quan.li@mail.foxconn.com,Sunshine.fan@mail.foxconn.com,thanh-ha.ngo@mail.foxconn.com,gong-quan.ruan@mail.foxconn.com,carl.yy.chen@mail.foxconn.com";
        sendMailFile(mail,  json, "MAIL" );
        inStream.close();
        file.delete();
    }

    public void sendMailFile(String listUser, String json, String system) throws JsonProcessingException {
        // String listUser = "cpe-vn-fii-sw@mail.foxconn.com,cpe-vn-fii-app@mail.foxconn.com";
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> tmpMap = new HashMap<>();

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("system", system);
        bodyMap.put("type", "TEXT");
        bodyMap.put("source", "MMS");
        bodyMap.put("from", "");
        bodyMap.put("toUser", listUser);
        bodyMap.put("toGroup", null);
//        bodyMap.put("message", objectMapper.writeValueAsString(tmpMap));
        bodyMap.put("message", json);
        String body = objectMapper.writeValueAsString(bodyMap);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange("https://10.224.81.70:6443/notify-service/api/notify", HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error("### uploadFile error ", e);
            //return false;
        }
    }

    private HSSFCellStyle createStyle(HSSFWorkbook workbook) {

        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }
}
