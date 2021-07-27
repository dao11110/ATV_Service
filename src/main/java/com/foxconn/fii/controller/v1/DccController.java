package com.foxconn.fii.controller.v1;

import com.foxconn.fii.data.primary.model.dcc.MaterialSheet;
import com.foxconn.fii.data.primary.model.dcc.ProcessSheet;
import com.foxconn.fii.data.primary.model.dcc.ProductSheet;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.DccReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dcc")
public class DccController {

    @Value("${data.path}")
    private String dataPath;

    @Value("${data.thumb}")
    private String dataThumb;

    @Value("${data.patho}")
    private String dataPathO;

    @Autowired
    private DccReportService dccReportService;

    @PostMapping("/process_application")
    public Object addProcessApplication(@RequestBody Map<String, Object> mInput){
        ProcessSheet mProcess = new ProcessSheet(mInput);
        return dccReportService.addDccProcess(mProcess);
    }

    @PostMapping("/product_application")
    public Object addProductApplication(@RequestBody Map<String, Object> mInput){
        ProductSheet mProduct = new ProductSheet(mInput);
        return dccReportService.addDccProduct(mProduct, "PRODUCT");
    }

    @PostMapping("/material_application")
    public Object addMaterialApplication(@RequestBody Map<String, Object> mInput){
        MaterialSheet mMaterial = new MaterialSheet(mInput);
        return dccReportService.addDccMaterial(mMaterial);
    }

    @GetMapping("/get_data")
    public Response statisticData(){
        return dccReportService.statisticData();
    }
}
