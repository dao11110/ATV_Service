package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.agile.AgileEcn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgileEcnRepository extends JpaRepository<AgileEcn, Integer> {
    @Query("SELECT DISTINCT abp.ecnNo " +
            "FROM AgileBomPn AS abp " +
            "LEFT JOIN AgileEcn AS ae ON abp.ecnNo = ae.ecnNo " +
            "WHERE ae.id IS NULL " +
            "AND abp.ecnNo IS NOT NULL ")
    List<String> jpqlGetListEcnRequest();
}