package com.foxconn.fii.service.impl;

import com.foxconn.fii.data.primary.model.DccApplicationMeta;
import com.foxconn.fii.data.primary.repository.DccApplicationMetaRepository;
import com.foxconn.fii.service.DccReportMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DccReportMetaServiceImpl implements DccReportMetaService {

    @Autowired
    private DccApplicationMetaRepository dccApplicationMetaRepository;

    @Override
    public DccApplicationMeta addDccReportMeta(Integer mIdDccReport, String mKey, String mValue, String mDesc) {
        DccApplicationMeta metaData = new DccApplicationMeta();
        metaData.setIdDcc(mIdDccReport);
        metaData.setMetaKey(mKey);
        metaData.setMetaValue(mValue);
        metaData.setDescription(mDesc);
        dccApplicationMetaRepository.save(metaData);
        return metaData;
    }
}
