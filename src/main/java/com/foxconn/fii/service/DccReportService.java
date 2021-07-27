package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.DccApplication;
import com.foxconn.fii.data.primary.model.dcc.MaterialSheet;
import com.foxconn.fii.data.primary.model.dcc.ProcessSheet;
import com.foxconn.fii.data.primary.model.dcc.Sheet;
import com.foxconn.fii.response.Response;

public interface DccReportService {
    DccApplication addDccProduct(Sheet mProduct, String mType);
    DccApplication addDccProcess(ProcessSheet mProcess);
    DccApplication addDccMaterial(MaterialSheet mMaterial);
    Response statisticData();
}
