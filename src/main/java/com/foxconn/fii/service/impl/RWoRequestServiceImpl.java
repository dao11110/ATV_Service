package com.foxconn.fii.service.impl;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import com.foxconn.fii.data.b04sfc.repository.B04RWoRequestRepository;
import com.foxconn.fii.data.f12sfc.model.F12RWoRequest;
import com.foxconn.fii.data.f12sfc.repository.F12RWoRequestRepository;
import com.foxconn.fii.data.primary.model.RWoRequest;
import com.foxconn.fii.data.primary.model.agile.AgileBomPn;
import com.foxconn.fii.data.primary.repository.AgileBomPnRepository;
import com.foxconn.fii.data.primary.repository.RWoRequestRepository;
import com.foxconn.fii.service.RWoRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RWoRequestServiceImpl implements RWoRequestService {

    @Autowired
    @Qualifier(value = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AgileBomPnRepository agileBomPnRepository;

    @Autowired
    private B04RWoRequestRepository b04RWoRequestRepository;

    @Autowired
    private F12RWoRequestRepository f12RWoRequestRepository;

    @Autowired
    private RWoRequestRepository rWoRequestRepository;

    @Override
    public List<Object> checkRWoRequestNew(String mFactory) {
        List<Map<String, Object>> maxTime = rWoRequestRepository.jpqlGetMaxTimeDonwload(mFactory);
        List<Object> mData = new ArrayList<>();
        if(maxTime.size() > 0){
            Date mTime = (Date) maxTime.get(0).get("max_time");
            if(mFactory.equals(DataStatic.ITSFC.FACTORY.B04)){
                List<B04RWoRequest> dataWoRequest = b04RWoRequestRepository.jpqlGetPnsByTime(mTime);
                if(dataWoRequest.size() > 0){
                    for(int i = 0; i < dataWoRequest.size(); i++){
                        RWoRequest item = new RWoRequest(dataWoRequest.get(i));
                        rWoRequestRepository.save(item);
                    }
                    mData.addAll(dataWoRequest);
                    if(dataWoRequest.size() > 0){

                    }
                }
            }else if(mFactory.equals(DataStatic.ITSFC.FACTORY.F12)){
                List<F12RWoRequest> dataWoRequest = f12RWoRequestRepository.jpqlGetPnsByTime(mTime);
                if(dataWoRequest.size() > 0){
                    for(int i = 0; i < dataWoRequest.size(); i++){
                        RWoRequest item = new RWoRequest(dataWoRequest.get(i));
                        rWoRequestRepository.save(item);
                    }
                    mData.addAll(dataWoRequest);
                }
            }
        }
        return mData;
    }

    @Override
    public List<AgileBomPn> readBomFileData(MultipartFile file) throws IOException {
        List<AgileBomPn> result = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        String mfr = "";
        String mType = "SI";
        String product = "";
        //start = 0 sheet.getPhysicalNumberOfRows()
        for(int i = 1 ; i < sheet.getPhysicalNumberOfRows(); i++){
            System.out.println("##########Row: "+i+" --");
            Row row = sheet.getRow(i);
            AgileBomPn itemPn = new AgileBomPn();
            if(!isRowEmpty(row, 1) && !isRowEmpty(row, 25)){
                String pn = row.getCell(1).toString().trim();
                String level = row.getCell(0).toString().trim();

                try{
                    String mTypeItem = "DOCUMENT";
                    String location = "";
                    if(!isRowEmpty(row, 27)){
                        mTypeItem = "PN";
                        location = row.getCell(27).toString().trim();
                    }
                    if(pn.indexOf("D00") > 0){
                        mType = "PTH";
                    }else if(pn.indexOf("S00") > 0){
                        mType = "SMT";
                    }
                    if(level.equals("1")){
                        mType = "SI";
                    }

                    if(!isRowEmpty(row, 40)){
                        itemPn = getDataBom(row);
                        itemPn.setIdBomVersion(1);
                        itemPn.setPn(pn);
                        itemPn.setProduct(product);
                        itemPn.setType(mType);
                        itemPn.setTypeItem(mTypeItem);
                        itemPn.setLocation(location);
                        itemPn.setMfr(mfr);
                        itemPn.setIdBomVersion(0);
                        mfr = "";
                        result.add(itemPn);
                    }else{
                        product = pn;
                    }
                    agileBomPnRepository.saveAll(result);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }else{
                if(i > 1){
                    String mfrName = row.getCell(40).toString().trim();
                    String mfrPn = row.getCell(41).toString().trim();
                    mfr += mfrName+": "+mfrPn+" ; ";
                }
            }
        }
        return result;
    }

    private boolean isRowEmpty(Row row, int col){
        Cell cell = row.getCell(col);
        if(cell != null && cell.getCellType() != CellType.BLANK){
            return false;
        }
        return true;
    }

    private AgileBomPn getDataBom(Row mRow){
        AgileBomPn itemPn = new AgileBomPn();
        itemPn.setDescription(mRow.getCell(3).toString().trim());
//        itemPn.setProductLine(mRow.getCell(7).toString().trim());
        itemPn.setRev(mRow.getCell(24).toString().trim());
        itemPn.setRevRelease(mRow.getCell(10).toString().trim());
        itemPn.setBomQty(mRow.getCell(25).toString().trim());
        itemPn.setBomFindNum(mRow.getCell(26).toString().trim());
        itemPn.setBomPlan(mRow.getCell(31).toString().trim());
//        itemPn.set(mRow.getCell(1).toString().trim());
        return itemPn;
    }
}
