package com.foxconn.fii.service.impl;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.primary.model.DccApplication;
import com.foxconn.fii.data.primary.model.DccApplicationMeta;
import com.foxconn.fii.data.primary.model.dcc.MaterialSheet;
import com.foxconn.fii.data.primary.model.dcc.ProcessSheet;
import com.foxconn.fii.data.primary.model.dcc.Sheet;
import com.foxconn.fii.data.primary.repository.DccApplicationMetaRepository;
import com.foxconn.fii.data.primary.repository.DccApplicationRepository;
import com.foxconn.fii.response.Response;
import com.foxconn.fii.service.DccReportMetaService;
import com.foxconn.fii.service.DccReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DccReportServiceImpl implements DccReportService {

    @Autowired
    private DccApplicationRepository dccApplicationRepository;
    
    @Autowired
    private DccApplicationMetaRepository dccApplicationMetaRepository;

    @Autowired
    private DccReportMetaService dccReportMetaService;
    
    @Override
    public DccApplication addDccProduct(Sheet mProduct, String mType) {
        DccApplication dccApplication = new DccApplication(mProduct);
        dccApplication.setType(mType);
        dccApplicationRepository.save(dccApplication);
        return dccApplication;
    }

    @Override
    public DccApplication addDccProcess(ProcessSheet mProcess) {
        DccApplication dccReport = addDccProduct(mProcess, "PROCESS");

        List<DccApplicationMeta> metaData = new ArrayList<>();

        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "PROCESS_BEFORE", mProcess.getProcessBefore(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "PROCESS_AFTER", mProcess.getProcessAfter(), ""));

        dccReport.setMetaData(metaData);

        return dccReport;
    }

    @Override
    public DccApplication addDccMaterial(MaterialSheet mMaterial) {
        DccApplication dccReport = addDccProduct(mMaterial, "MATERIAL");

        List<DccApplicationMeta> metaData = new ArrayList<>();

        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "MFG_NO", mMaterial.getMfgNo(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "QTY", mMaterial.getQty(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "CUSTOMER_NO", mMaterial.getCustomerNo(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "MFG_NAME", mMaterial.getMfgName(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "DC", mMaterial.getDc(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "MATERIAL_NAME", mMaterial.getMaterialName(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "CHARGE_VENDOR", mMaterial.getIsChargeVendor(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "IPQ_NO", mMaterial.getIsIpqNo(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "BUY_PART", mMaterial.getIsBuyPart(), ""));
        metaData.add(dccReportMetaService.addDccReportMeta(dccReport.getId(), "MRB", mMaterial.getIsMrb(), ""));

        dccReport.setMetaData(metaData);

        return dccReport;
    }

    @Override
    public Response statisticData() {
        List<String> mData = dccApplicationRepository.jpqlGetListDocNo();
        if (mData.size() > 0) {
            return new Response(DataStatic.Status.SUCCESS, "Load data successful", mData, mData.size());
        } else {
            return new Response(DataStatic.Status.SUCCESS, "Data null", null, 0);
        }
    }
}
