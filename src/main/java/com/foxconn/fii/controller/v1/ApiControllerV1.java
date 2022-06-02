package com.foxconn.fii.controller.v1;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.b04tensioning.repository.TTensioningRepository;
import com.foxconn.fii.data.primary.repository.RWoRequestRepository;
import com.foxconn.fii.request.hr.UserCovid;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.*;
import com.foxconn.fii.service.hr.CovidService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

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
    private RWoRequestService rWoRequestService;

    @Autowired
    private RWoRequestRepository rWoRequestRepository;

    @Autowired
    private RFaiSmtConfigService rFaiSmtConfigService;

    @Autowired
    private RFaiSmtStationService rFaiSmtStationService;

    @Autowired
    private PaperLogService paperLogService;

    @Autowired
    private AgileBomService agileBomService;

    @Autowired
    private AgileEcnService agileEcnService;

    @Autowired
    private CovidService covidService;

    @Autowired
    private MailService mailService;

    @Autowired
    TTensioningRepository tTensioningReportsitory;

    private void resize(String path, String subFolder) throws IOException {
        File file = new File(path);
        if(file.exists()){
            String nameFile = file.getName();

            BufferedImage img = ImageIO.read(file);
            BufferedImage thumb = Scalr.resize(img, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 100, 50, Scalr.OP_ANTIALIAS);

            File f2 = new File(dataThumb+subFolder+nameFile);
            ImageIO.write(thumb, "jpg", f2);
            log.info("resize: "+dataThumb+subFolder+nameFile);
        }
    }

    private void readFile() throws IOException {
        File folder = new File(dataPathO);
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++){
            BasicFileAttributes fatr = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
            Date time = new Date();
            time.setTime(fatr.creationTime().toMillis());
            checkSubFolder(DataStatic.FILE.SUB_FOLDER(time));
            if(files[i].isFile()){
                resize(files[i].getPath(), DataStatic.FILE.SUB_FOLDER(time));
            }else{

            }
            System.out.println("Detail: "+fatr.creationTime()+" - "+time.toString()+" - "+files[i].getPath()+" - "+files[i].getName()+" - "+time+"");
        }
    }

    private void readFile(List<Map<String, Object>> mData) throws IOException {
        for(int i = 0; i < mData.size(); i++){
            Date time = (Date) mData.get(i).get("time");
            checkSubFolder(DataStatic.FILE.SUB_FOLDER(time));
            String pathUrl = dataPath + (String) mData.get(i).get("url");
            resize(pathUrl, DataStatic.FILE.SUB_FOLDER(time));
        }
    }

    public boolean checkTypeFileIsImage(File mFile){
        String typeFile = FilenameUtils.getExtension(mFile.getPath());
        if (typeFile.isEmpty() || typeFile.trim().length() == 0){
            File fileExtension = new File(mFile.getPath()+".jpg");
            mFile.renameTo(fileExtension);
            return false;
        }
        return true;
    }

    private void checkSubFolder(String mSubFolder){
//        File theMedia = new File(dataPath+mSubFolder);
        File theThumb = new File(dataThumb+mSubFolder);
//        if (!theMedia.exists()){
//            theMedia.mkdirs();
//            System.out.println("Media");
//        }
        if (!theThumb.exists()){
            theThumb.mkdirs();
            System.out.println("Thumb");
        }
    }

    @GetMapping("/test")
    public Object test() throws Exception {
//        return agileBomService.downloadBomAgile(modelName);
//        return agileEcnService.checkAndDownloadEcn();
        return tTensioningReportsitory.jpqlGetTTensioning(1);
//        Map<String, Object> mMap = new HashMap<>();
//        mMap.put("V0959579", "Test");
//        mMap.put("v0959579", "test 1");
//        return mMap;
    }

    @GetMapping("/get_data_tension_in_out")
    public  Object getDataTensionInOut(@RequestParam("flagType") Integer flagType){
        List<Map<String,Object>>mData= tTensioningReportsitory.jpqlGetTTensioning(flagType);
        if (mData.size()>0){
            return new Response(DataStatic.Status.SUCCESS, "Load data success", mData, mData.size());
        }else {
            return new Response(DataStatic.Status.FAIL, "Load data fail", 0, 0);
        }
    }

    @GetMapping("/test_2")
    public Object test2(TimeSpan timeSpan) throws IOException {
        Map<String, Object> mData = covidService.checkUserCovid(timeSpan);
        List<UserCovid> mUser = (List<UserCovid>) mData.get("data_check");
        if(mUser.size() > 0){
            Integer oppm = (Integer) mData.get("total_oppm");
            Integer noData = (Integer) mData.get("total_no_data");
            String subContent = "<b>Statistic</b><br>" +
                    "- Total Employee: "+oppm.intValue()+" <br>" +
                    "- Have temperature in system: "+(oppm.intValue()-noData.intValue())+" <br>" +
                    "<span style='color:#CA5100'>- Don't Have temperature in system: "+noData.intValue()+"</span>";
//            mailService.sendMailAndFile(mUser,subContent);
        }
        return mData;
    }

    @PostMapping("/update_status_fai")
    public Response updateStatusFai(@RequestBody List<Map<String, String>> data){
        String log = "";
        if(data.size() > 0){
            for(int i = 0; i < data.size(); i++){
                String station = data.get(i).get("station");
                String wo = data.get(i).get("wo");
                boolean check = rFaiSmtConfigService.updateStatusFai(station,wo);
                log += "{STATION:"+station+", WO:"+wo+", STATUS:"+check+"},";
            }
        }

        paperLogService.addLog("Paperless(C02-Allpart-SMT)", log, "UPDATE DATA", "C02", "QA");
        return new Response(DataStatic.Status.SUCCESS, "Load data success", new ArrayList<>(), 0);
    }

    @PostMapping("/upfile_bom")
    public Object upfileBom(@RequestParam MultipartFile file) throws IOException {
        return rWoRequestService.readBomFileData(file);
    }


}
