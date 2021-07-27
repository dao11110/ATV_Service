package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.PaperLog;

public interface PaperLogService {
     PaperLog addLog(String name, String log, String func, String factory, String team);
}
