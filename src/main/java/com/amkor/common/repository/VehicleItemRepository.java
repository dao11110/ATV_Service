package com.amkor.common.repository;

import com.amkor.models.VehicleItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface VehicleItemRepository extends JpaRepository<VehicleItemModel,Integer> {
    @Query(value = "select * from  vehicle_item where id_header=:idHeader order by seq asc",nativeQuery = true)
    ArrayList<VehicleItemModel> findByIdHeader(int idHeader);
}
