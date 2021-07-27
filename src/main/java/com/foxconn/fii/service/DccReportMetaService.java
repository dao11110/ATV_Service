package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.DccApplicationMeta;

public interface DccReportMetaService {
    DccApplicationMeta addDccReportMeta(Integer mIdDccReport, String mKey, String mValue, String mDesc);
}
