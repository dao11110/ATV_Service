package com.foxconn.fii.service.hr;

import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.request.hr.UserCovid;

import java.util.Map;

public interface CovidService {
    Map<String, UserCovid> getInfoUserFromOppm(TimeSpan timeSpan);
    Map<String, Object> checkUserCovid(TimeSpan timeSpan);
}
