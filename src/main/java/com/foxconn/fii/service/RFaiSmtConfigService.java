package com.foxconn.fii.service;

import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.response.Response;

import java.util.List;
import java.util.Map;

public interface RFaiSmtConfigService {
    Response checkDataWoB04(TimeSpan timeSpan, String mFactory);
    Response checkDataWoF12(TimeSpan timeSpan, String mFactory);

    boolean updateStatusFai(String station, String wo);

    List<Map<String, Object>> getListMedia(TimeSpan timeSpan);

    Object test(String wo);
}
