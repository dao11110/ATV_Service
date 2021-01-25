package com.foxconn.fii.service;

import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import com.foxconn.fii.request.b04sfc.WO;
import com.foxconn.fii.response.Response;

import java.util.List;
import java.util.Map;

public interface B04Service {
    List<Map<String, Object>> getEcnNoByWo(WO mData);
    List<Map<String, Object>> getEcnNoByWo(RSmtFaiConfig mData);
    List<Map<String, Object>> getMaterialSolderByWo(RSmtFaiConfig mData);
    List<Map<String, Object>> getMaterialSolderByWo(WO mData);
}
