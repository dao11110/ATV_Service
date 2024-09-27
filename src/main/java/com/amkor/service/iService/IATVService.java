package com.amkor.service.iService;

import com.amkor.models.AlertForFGModel;
import com.amkor.models.AutoLabelModel;
import com.amkor.models.OnLineScheduleSheetFileModel;
import com.amkor.models.ProcessNoteModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IATVService extends IReadService {
    List<AlertForFGModel> getAlertForFGNotScheduledFor30Days(int factoryId, String plant, String cust);

    boolean checkExistProcessNote(ProcessNoteModel model);

    boolean checkExistAutoLabel(AutoLabelModel model);

    OnLineScheduleSheetFileModel getOnlineScheduleSheetMemoFileFromStationAndLotName(String station, String lotName);
}
