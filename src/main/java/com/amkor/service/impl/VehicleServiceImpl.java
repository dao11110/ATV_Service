package com.amkor.service.impl;

import com.amkor.common.repository.VehicleHeaderRepository;
import com.amkor.common.repository.VehicleItemRepository;
import com.amkor.models.VehicleHeaderModel;
import com.amkor.models.VehicleItemModel;
import com.amkor.service.iService.IVehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class VehicleServiceImpl implements IVehicleService {
    @Autowired
    VehicleHeaderRepository vehicleHeaderRepository;

    @Autowired
    VehicleItemRepository vehicleItemRepository;


    @Override
    public ArrayList<VehicleHeaderModel> getListVehicle() {
        return vehicleHeaderRepository.findAll();
    }

    @Override
    public int saveVehiclePre(VehicleHeaderModel model) {
        int result = vehicleHeaderRepository.save(model).getId();
        return result;
    }

    @Override
    public int saveVehicleItem(ArrayList<Map<String,Object>> listData, int idHeader) {
        int result = 0;
        for (Map<String,Object> data : listData) {
            VehicleItemModel model=new VehicleItemModel();
            model.setIdHeader(idHeader);
            model.setCartonNo((Integer) data.get("cartonNo"));
            model.setCartonSequence((Integer) data.get("cartonSequence"));
            model.setCustCode((Integer) data.get("custCode"));
            model.setCustName((String) data.get("custName"));
            model.setForwarderCode((String) data.get("forwarderCode"));
            model.setInvoiceNo((String) data.get("invoiceNo"));
            model.setLicenseNumber((String) data.get("licenseNumber"));
            model.setPhoneNumber((String) data.get("phoneNumber"));
            model.setPickupLocation((String)  data.get("pickupLocation"));
            model.setSeq((Integer) data.get("seq"));
            model.setSeqBtnColor((Integer) data.get("intColor"));
            model.setShipmentDate(String.valueOf((Integer) data.get("shipmentDate")));
            model.setShippingPlant((String) data.get("shippingPlant"));
            model.setUserID((String) data.get("userID"));
            model.setUserName((String) data.get("userName"));


            result = vehicleItemRepository.save(model).getId();
            if (result == 0) {
                break;
            }
        }
        return result;
    }

    @Override
    public ArrayList<VehicleItemModel> findVehicleItemByID(int id) {
        return vehicleItemRepository.findByIdHeader(id);
    }

    @Override
    public int updateVehiclePre(String userUpdate, int id) {
        return vehicleHeaderRepository.updateVehicleHeader(userUpdate,id);
    }

    @Override
    public ArrayList<VehicleHeaderModel> checkExistedData(String visitor, String invoice, String fwdr) {
        return vehicleHeaderRepository.checkExistedData(visitor,invoice,fwdr);
    }


}
