package com.foxconn.fii.service;

import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.common.response.ListResponse;
import com.foxconn.fii.response.Response;

import java.util.List;
import java.util.Map;

public interface RFaiSmtConfigService {
    Response checkNewDataWo(TimeSpan timeSpan);

    Object checkDataTest();

    List<Map<String, Object>> getListMedia(TimeSpan timeSpan);

    Object testFunc();
}
