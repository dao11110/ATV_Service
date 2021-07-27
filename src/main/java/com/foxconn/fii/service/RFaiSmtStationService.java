package com.foxconn.fii.service;

import com.foxconn.fii.response.Response;

public interface RFaiSmtStationService {
    Response getStationInformationByWo(String mFactory, String mWo, Integer idConfig, String lineName);
}
