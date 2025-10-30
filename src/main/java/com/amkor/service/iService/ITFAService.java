package com.amkor.service.iService;

import com.amkor.models.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ITFAService extends IWriteService {
    boolean sendMailProcess(String title, String sContent, List<String> toPeople, List<String> ccPeople, List<String> fileNames);

    int updateProcessNote(ProcessNoteModel model);

    int updateAutoLabel(AutoLabelModel model);

    int createAutoLabelMaintenance(AutoLabelModel model);

    int createProcessNote(ProcessNoteModel model);

    boolean checkExistProcessNote(ProcessNoteModel model);

    boolean checkExistAutoLabel(AutoLabelModel model);

    OnLineScheduleSheetFileModel getOnlineScheduleSheetMemoFileFromStationAndLotName(String station, String lotName);

    String holdLot(String lotName, String lotDcc, String holdCode, String holdReason, String userBadge);

    String releaseLot(String lotName, String lotDcc, String holdCode, String releaseReason, String userBadge, int holdOpr, long shipBackDate);

    List<DateCodeDiscrepancyModel> getDateCodeDiscrepancy();

    String sendMailReportDateCodeDiscrepancyChecking(Map<String, Object> body);

}
