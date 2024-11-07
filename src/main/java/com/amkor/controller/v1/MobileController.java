package com.amkor.controller.v1;


import com.amkor.models.VehicleHeaderModel;
import com.amkor.models.VehicleItemModel;
import com.amkor.service.impl.VehicleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
public class MobileController {
    @Autowired
    VehicleServiceImpl vehicleServiceImpl;


    @RequestMapping(method = RequestMethod.GET, value = "/getListVehiclePre")
    public ArrayList<VehicleHeaderModel> getListVehiclePre() {
        return vehicleServiceImpl.getListVehicle();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findVehicleItemByIdHeader")
    public ArrayList<VehicleItemModel> findVehicleItemByIdHeader(int idHeader) {
        return vehicleServiceImpl.findVehicleItemByID(idHeader);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveVehiclePre")
    public String saveVehiclePre(@RequestBody Map<String, Object> data) {
        int idHeader = 0;
        String result = "Save Vehicle Fail";
        if (data != null) {
            VehicleHeaderModel model = new VehicleHeaderModel();
            ArrayList<Map<String, Object>> listItem = new ArrayList<>();
            Map<String, Object> dataHeader = new HashMap<>();
            listItem = (ArrayList<Map<String, Object>>) data.get("Item");
            dataHeader = (Map<String, Object>) data.get("Header");
            model.setInvoice((String) dataHeader.get("invoice"));
            model.setAgency((String) dataHeader.get("agency"));
            model.setBizType((Integer) dataHeader.get("bizType"));
            model.setCreateBy((String) dataHeader.get("createBy"));
//            model.setCreatedAt((String) dataHeader.get("createdAt"));
            model.setCusCode((Integer) dataHeader.get("cusCode"));
            model.setDateFrom((String) dataHeader.get("dateFrom"));
            model.setDateTo((String) dataHeader.get("dateTo"));
            model.setFwdr((String) dataHeader.get("fwdr"));
            model.setLocation((String) dataHeader.get("location"));
            model.setNation((int) dataHeader.get("nation"));
            model.setPlant((String) dataHeader.get("plant"));
            model.setRegion((Integer) dataHeader.get("region"));
            model.setSequenceFrom((Integer) dataHeader.get("sequenceFrom"));
            model.setSequenceTo((Integer) dataHeader.get("sequenceTo"));
            model.setUpdateBy((String) dataHeader.get("updateBy"));
            model.setVisitor((String) dataHeader.get("visitor"));
            model.setCreatedAt(getTimeDateCurrent());
            model.setUpdatedAt(getTimeDateCurrent());
            model.setStatus(1);
            if (checkExistedData(model.getVisitor(), model.getInvoice(), model.getFwdr()) > 0) {
                result = "The data is existed";
            } else {
                idHeader = vehicleServiceImpl.saveVehiclePre(model);
                if ((idHeader > 0)) {

                    vehicleServiceImpl.saveVehicleItem(listItem, idHeader);


                    result = "Save Vehicle Success";
                }
            }

        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateVehiclePre")
    public String updateVehiclePre(@RequestParam("userUpdate") String userUpdate, @RequestParam("id") int id) {
        String result = "Update Vehicle Pre Fail";
        int update = vehicleServiceImpl.updateVehiclePre(userUpdate, id);
        if (update > 0) {
            result = "Update Vehicle Pre Success";
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/checkExistedData")
    public int checkExistedData(String visitor, String invoice, String fwdr) {
        return vehicleServiceImpl.checkExistedData(visitor, invoice, fwdr).size();
    }

    public static Date getTimeDateCurrent() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
}
