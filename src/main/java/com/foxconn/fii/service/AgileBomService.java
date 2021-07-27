package com.foxconn.fii.service;

import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import com.foxconn.fii.response.Response;

import java.util.List;
import java.util.Map;

public interface AgileBomService {
    List<Map<String, Object>> requestToAgile(String modelName);
    Response sycnDataAgile(List<Map<String, Object>> mData, String mModelName);
    Object downloadBoms();
    Object checkVersionBomAgileAndSap(List<B04RWoRequest> mListPn);
}
