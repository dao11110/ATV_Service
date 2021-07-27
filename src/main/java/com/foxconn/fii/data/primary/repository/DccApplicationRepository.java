package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.DccApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DccApplicationRepository extends JpaRepository<DccApplication, Integer> {
    @Query("SELECT docNo AS doc " +
            "FROM DccApplication da ")
    List<String> jpqlGetListDocNo();
}
