package com.foxconn.fii.service.impl;

import com.foxconn.fii.data.primary.model.PaperLog;
import com.foxconn.fii.data.primary.repository.PaperLogRepository;
import com.foxconn.fii.service.PaperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaperLogServiceImpl implements PaperLogService {

    @Autowired
    private PaperLogRepository paperLogRepository;

    @Override
    public PaperLog addLog(String name, String log, String func, String factory, String team) {
        PaperLog item = new PaperLog();
        item.setName(name);
        item.setLog(log);
        item.setFunction(func);
        item.setFactory(factory);
        item.setTeam(team);
        paperLogRepository.save(item);
        return item;
    }
}
