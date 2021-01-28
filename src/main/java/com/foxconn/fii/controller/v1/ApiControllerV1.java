package com.foxconn.fii.controller.v1;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.common.response.ResponseCode;
import com.foxconn.fii.data.b04stencil.model.TTensioning;
import com.foxconn.fii.data.b04stencil.repository.TTensioningRepository;
import com.foxconn.fii.service.MailService;
import com.foxconn.fii.service.RFaiSmtConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ApiControllerV1 {

    @Value("${data.path}")
    private String dataPath;

    @Value("${data.thumb}")
    private String dataThumb;

    @Value("${data.patho}")
    private String dataPathO;

    @Autowired
    private RFaiSmtConfigService rFaiSmtConfig;

    @Autowired
    private MailService mailService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private TTensioningRepository TTensioningRepository;

    private void resize(String path, String subFolder) throws IOException {
        File file = new File(path);
        if (file.exists()) {
//            boolean isImage = checkTypeFileIsImage(file);
            String nameFile = file.getName();
//            if(!isImage){
//                nameFile += ".jpg";
//            }
//            file = new File(dataPathO+nameFile);
//            File tmpFile = new File(dataPath+subFolder+nameFile);
//            Files.copy(file.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            BufferedImage img = ImageIO.read(file);
            BufferedImage thumb = Scalr.resize(img, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 100, 50, Scalr.OP_ANTIALIAS);

            File f2 = new File(dataThumb + subFolder + nameFile);
            ImageIO.write(thumb, "jpg", f2);
            log.info("resize: " + dataThumb + subFolder + nameFile);
        }
    }

    private void readFile() throws IOException {
        File folder = new File(dataPathO);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            BasicFileAttributes fatr = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
            Date time = new Date();
            time.setTime(fatr.creationTime().toMillis());
            checkSubFolder(DataStatic.FILE.SUB_FOLDER(time));
            if (files[i].isFile()) {
                resize(files[i].getPath(), DataStatic.FILE.SUB_FOLDER(time));
            } else {

            }
            System.out.println("Detail: " + fatr.creationTime() + " - " + time.toString() + " - " + files[i].getPath() + " - " + files[i].getName() + " - " + time + "");
        }
    }

    private void readFile(List<Map<String, Object>> mData) throws IOException {
        for (int i = 0; i < mData.size(); i++) {
            Date time = (Date) mData.get(i).get("time");
            checkSubFolder(DataStatic.FILE.SUB_FOLDER(time));
            String pathUrl = dataPath + (String) mData.get(i).get("url");
//            System.out.println("URL: "+pathUrl);
            resize(pathUrl, DataStatic.FILE.SUB_FOLDER(time));
        }
    }

    public boolean checkTypeFileIsImage(File mFile) {
        String typeFile = FilenameUtils.getExtension(mFile.getPath());
        if (typeFile.isEmpty() || typeFile.trim().length() == 0) {
            File fileExtension = new File(mFile.getPath() + ".jpg");
            mFile.renameTo(fileExtension);
            return false;
        }
        return true;
    }

    private void checkSubFolder(String mSubFolder) {
//        File theMedia = new File(dataPath+mSubFolder);
        File theThumb = new File(dataThumb + mSubFolder);
//        if (!theMedia.exists()){
//            theMedia.mkdirs();
//            System.out.println("Media");
//        }
        if (!theThumb.exists()) {
            theThumb.mkdirs();
            System.out.println("Thumb");
        }
    }

    @PostMapping("/test")
    public Object test(@RequestParam(name = "time_span", required = false) TimeSpan timeSpan
            , @RequestParam(name = "input_1") String input1
            , @RequestParam(name = "input_2") String input2) throws IOException {
        readFile();
        return 1;
    }

    @PostMapping("/test_2")
    public Object test2(@RequestParam(name = "time_span") TimeSpan timeSpan) throws IOException {
        readFile(rFaiSmtConfig.getListMedia(timeSpan));
        return 1;
    }


//    10.224.83.55,3000
//    sa
//    congcong

    //PaperlessStencil by VIE
    @GetMapping("/get_paper_tension_send")
    public CommonResponse<Map<String, Object>> paperlessStencilList() {
        Map<String, Object> mData = TTensioningRepository.jpqlGetTopTTensioning();
        if (mData != null) {
            if(mData.get("Result").toString().equals("PASS")){
                Map<String, Object> objectMap = new LinkedHashMap<>();
                objectMap.put("step_1", "OK");
                objectMap.put("step_2", "OK");
                String value = "";
                for(int i = 1; i<10; i++){
                    String value_n = mData.get("Value_"+ i).toString().trim();
                    if(value_n.equals("null") || Float.parseFloat(value_n) < 27.0)
                        return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Data Fail!", mData);
                    else {
                        if (i == 1)
                            value += mData.get("Value_" + i).toString();
                        value += " - " + mData.get("Value_" + i).toString();
                    }
                }
                objectMap.put("step_3", value);
                Map<String, Object> dataAllPart = dataAllPart(mData.get("Model").toString().trim());
                if(dataAllPart != null){
                    objectMap.put("step_4", dataAllPart.get("").toString());
                    objectMap.put("step_5", dataAllPart.get("").toString());
                    objectMap.put("step_6", dataAllPart.get("").toString());
                    objectMap.put("step_7", dataAllPart.get("").toString());
                    objectMap.put("step_8", dataAllPart.get("").toString());
                    return CommonResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "Get Data Successful", objectMap);
                }else
                    return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Get Data IT AllPart Fail", objectMap);
            }
            else
                return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Result Fail!", mData);
        } else
            return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Get Data Fail", null);
    }

    @GetMapping("/get_paper_tension_receive")
    public CommonResponse<Map<String, Object>> getPaperTensionReceive() {
        Map<String, Object> mData = TTensioningRepository.jpqlGetTopTTensioning();
        if (mData != null) {
            if(mData.get("Result").toString().equals("PASS")){
                Map<String, Object> objectMap = new LinkedHashMap<>();
                objectMap.put("step_1", "OK");
                objectMap.put("step_2", "OK");
                objectMap.put("step_3", "OK");
                objectMap.put("step_4", "OK");
                Map<String, Object> dataAllPart = dataAllPart(mData.get("Model").toString().trim());
                if(dataAllPart != null){
                    objectMap.put("step_5", dataAllPart.get("").toString());
                    objectMap.put("step_6", dataAllPart.get("").toString());
                    objectMap.put("step_7", dataAllPart.get("").toString());
                    objectMap.put("step_8", dataAllPart.get("").toString());
                    return CommonResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "Get Data Successful", objectMap);
                }else
                    return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Get Data IT AllPart Fail", objectMap);
            }
            else
                return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Result Fail!", mData);
        } else
            return CommonResponse.of(HttpStatus.OK, ResponseCode.FAILED, "Get Data Fail", null);
    }

    public Map<String, Object> dataAllPart(String Model) {
        Map<String, Object> map = new LinkedHashMap<>();
        return map;
    }
}
