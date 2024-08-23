package com.amkor.service;

import com.amkor.common.repository.APILoggingRepository;
import com.amkor.models.ATVNetAPILoggingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APILoggingService {

    @Autowired
    private APILoggingRepository repo;

    public List<ATVNetAPILoggingModel> getAll() {
        return repo.findAll();
    }

    public ATVNetAPILoggingModel insertLog(ATVNetAPILoggingModel model) {
        return repo.save(model);
    }
}
