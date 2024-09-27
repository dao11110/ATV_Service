package com.amkor.service;

import com.amkor.common.repository.MiscTableRepository;
import com.amkor.models.ATVNetMiscTableModel;
import com.amkor.service.iService.IReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ATVNetMiscTableService implements IReadService {

    @Autowired
    private MiscTableRepository repo;

    // over load get List method
    public List<ATVNetMiscTableModel> getList(int factoryId, String tableId, String tableCode1, String tableCode2) {
        return repo.getList(factoryId, tableId, tableCode1, tableCode2);
    }

    public ATVNetMiscTableModel getOne(int factoryId, String tableId, String tableCode1, String tableCode2) {
        return repo.getOne(factoryId, tableId, tableCode1, tableCode2);
    }
}
