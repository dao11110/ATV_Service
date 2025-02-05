package com.amkor.controller.v1;

import com.amkor.service.ATVService;
import com.amkor.service.SAPService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/app")
public class ApiControllerV1 {

    @Value("${data.path}")
    private String dataPath;

    @Value("${data.thumb}")
    private String dataThumb;

    @Value("${data.patho}")
    private String dataPathO;
    @Autowired
    private ATVService atvService;
    @Autowired
    private SAPService sapService;
    @Autowired
    Data400Controller data400Controller;


    @GetMapping("/test")
    public String test() throws Exception {
//        return agileBomService.downloadBomAgile(modelName);
//        return agileEcnService.checkAndDownloadEcn();
//        return tTensioningReportsitory.jpqlGetTTensioning(1);
//        Map<String, Object> mMap = new HashMap<>();
//        mMap.put("V0959579", "Test");
//        mMap.put("v0959579", "test 1");
        return "mMap";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/sendMail")
    public String uploadFile(@RequestParam(value = "files") List<MultipartFile> files,
                             @RequestParam(value = "to", required = false) String to,
                             @RequestParam(value = "cc", required = false) String cc,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "opinion", required = false) String opinion) {
        String result = "false";
        if (files.size() > 0) {


            List<String> listFile = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {

                String filePath = "C:\\Dao\\App\\";
                String DomainName = getDomainName();
                String originalName;
                if (DomainName.equals("localhost")) {
                    filePath = "c:/temp";
                }

                checkExistFolder(filePath);
                filePath = filePath + "/" + currentDate();
                checkExistFolder(filePath);

                originalName = files.get(i).getOriginalFilename();
                filePath = filePath + '/' + originalName;

                File fileUpload = new File(filePath);
//                log.debug("aaaa" + fileUpload.getAbsolutePath());
                try {
                    files.get(i).transferTo(fileUpload);
                    // result = fileUpload.getAbsolutePath();
                    listFile.add(fileUpload.getPath());
                    result = "true";
                } catch (IOException e) {
                    result = "false";
                    throw new RuntimeException(e);
                }
            }
            if (result.equals("true")) {
                List<String> listTo = new ArrayList<>();
                List<String> listCC = new ArrayList<>();
                for (String s : to.split(",")) {
                    if (!s.isEmpty()) {
                        listTo.add(s);
                    }
                }
                for (String s : cc.split(",")) {
                    if (!s.isEmpty()) {
                        listCC.add(s);
                    }
                }

                try {
                    atvService.sendMailProcess2(title, listTo, listCC, listFile, opinion, false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/readeFileCSV")
    public String readFileCSV() {
        String result = "OK";
        String csvFile = "C:\\Users\\700063\\Downloads/ATV_GG01_20240610142500.csv";
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int i = 0;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);
                result = line;
//                System.out.println(country[15]); // This will print the second column value
//                System.out.println(country[20]); // This will print the second column value
//                System.out.println(country[25]); // This will print the second column value
                i++;
                int n = country.length / 5;
                for (int j = 3; j < n; j++) {
                    System.out.println(country[j * 5]);
                }
                if (i == 1) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void checkExistFolder(String fileChild) {
        File folder = new File(fileChild);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public String getDomainName() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String DomainName = req.getServerName();

        return DomainName;
    }

    private String currentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);
        return currentDate;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFileName")
    public String getFileName() {
        List<CSVRecord> records = sapService.checkFTPFile();
        String tableCode1 = "";
        String tableCode2 = "";
        String createTimeConvert = "";
        String mainDateTime = "";
        if (records.size() > 0) {
            tableCode1 = records.get(0).get("table code 01");
            tableCode2 = records.get(0).get("table code 02");
            String createTime = records.get(0).get("Create time");
            mainDateTime = records.get(0).get("Maint Date time");
            SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat dt1 = new SimpleDateFormat("yyyyMMdd");

            try {
                Date date = dt.parse(createTime);
                createTimeConvert = dt1.format(date);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        long timeMaxSAPCreate=data400Controller.getTimeSAPInsert();
        if (Long.parseLong(mainDateTime)>timeMaxSAPCreate){
            System.out.println("1234---1111");
            data400Controller.insertSAPRate(tableCode1,tableCode2,Long.parseLong(createTimeConvert),Long.parseLong(mainDateTime));
        }
        return tableCode1 + "____" + tableCode2 + "____" + createTimeConvert + "____" + mainDateTime+"____"+timeMaxSAPCreate;
    }
}
