package com.amkor.service.iService;

import com.amkor.models.ApiLoggingModel;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.ProcessNoteModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IATVThanhService extends IWriteService {
    boolean sendMailProcess(String title, String sContent, List<String> toPeople, List<String> ccPeople, List<String> fileNames);

    int updateProcessNote(ProcessNoteModel model);

    int updateAutoLabel(AutoLabelModel model);

    int createAutoLabelMaintenance(AutoLabelModel model);

    int createProcessNote(ProcessNoteModel model);

    int addApiLogging(ApiLoggingModel model);
}
