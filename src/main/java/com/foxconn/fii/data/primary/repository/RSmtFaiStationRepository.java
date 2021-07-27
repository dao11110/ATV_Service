package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.RSmtFaiStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RSmtFaiStationRepository extends JpaRepository<RSmtFaiStation, Integer> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE rsfs\n" +
            "\tSET rsfs.id_qc = mqs.id_qc\n" +
            "\tFROM r_smt_fai_station rsfs\n" +
            "\tINNER JOIN map_qc_sfc mqs ON rsfs.station = mqs.station " +
            "WHERE rsfs.id IN :ids", nativeQuery = true)
    int jpqlUpdateIdQcMapByIds(@Param("ids") List<Integer> ids);

}
