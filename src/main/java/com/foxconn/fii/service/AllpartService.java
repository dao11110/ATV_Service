package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.request.b04sfc.WO;

import java.util.List;
import java.util.Map;

public interface AllpartService {
    List<Map<String, Object>> getMaterialSolderByWo(RSmtFaiConfig mData, String mFactory);
    Map<String, Object> dataRoSHByWO(RSmtFaiConfig mItem, String mFactory);
    String dataEcnNo(RSmtFaiConfig mItem, String mFactory);
    List<Map<String, Object>> getEcnNoByWo(RSmtFaiConfig mData, String mFactory);
    List<Map<String, Object>> getMachineByWo(WO mData, String mFactory);
}
