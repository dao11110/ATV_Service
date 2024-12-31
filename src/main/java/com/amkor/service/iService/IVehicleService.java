package com.amkor.service.iService;

import com.amkor.models.VehicleHeaderModel;
import com.amkor.models.VehicleItemModel;

import java.util.ArrayList;
import java.util.Map;


public interface IVehicleService {
    ArrayList<VehicleHeaderModel> getListVehicle();
    int saveVehiclePre(VehicleHeaderModel model);

    int saveVehicleItem(ArrayList<Map<String,Object>>listData, int idHeader);
    ArrayList<VehicleItemModel>findVehicleItemByID(int id);

    int updateVehiclePre(String userUpdate,int id);
    ArrayList<VehicleHeaderModel> checkExistedData(String visitor,String invoice,String fwdr);
    ArrayList<VehicleHeaderModel> checkExistedData(String invoice,String fwdr);
}
