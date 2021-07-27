package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.agile.AgileEcn;

public interface AgileEcnService {
    AgileEcn requestToAgile(String mEcnNo);
    Object checkAndDownloadEcn();
}
