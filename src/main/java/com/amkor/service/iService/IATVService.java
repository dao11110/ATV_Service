package com.amkor.service.iService;

import com.amkor.models.AlertForFGModel;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.ProcessNoteModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IATVService extends IReadService {
    public List<AlertForFGModel> getAlertForFGNotScheduledFor30Days(int factoryId, String plant);

    boolean checkExistProcessNote(ProcessNoteModel model);

    boolean checkExistAutoLabel(AutoLabelModel model);
}
