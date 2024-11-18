package com.amkor.common.repository;

import com.amkor.models.VehicleHeaderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Repository
public interface VehicleHeaderRepository extends JpaRepository<VehicleHeaderModel, Integer> {

    @Query(value = "Select * from vehicle_header where status=1 ORDER BY id DESC",nativeQuery = true)
    ArrayList<VehicleHeaderModel>getVehicleHeaderModelList();

    ArrayList<VehicleHeaderModel>findAll();

    @Modifying
    @Transactional
    @Query(value = " Update vehicle_header set status=2,update_by=:userUpdate where id=:id",nativeQuery = true)
    int updateVehicleHeader(String userUpdate, int id);


    @Query(value = " Select * from vehicle_header where visitor=:visitor and invoice=:invoice and fwdr=:fwdr ",nativeQuery = true)
    ArrayList<VehicleHeaderModel> checkExistedData(String visitor,String invoice,String fwdr);

    @Query(value = " Select * from vehicle_header where  invoice=:invoice and fwdr=:fwdr ",nativeQuery = true)
    ArrayList<VehicleHeaderModel> checkExistedData(String invoice,String fwdr);
}
