package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.agile.AgileBomPn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgileBomPnRepository extends JpaRepository<AgileBomPn, Integer> {
    @Query("SELECT bpT.pn AS pn " +
            "FROM AgileBomPn AS bpT " +
            "WHERE bpT.type = :type " +
            "AND bpT.typeItem = :type_item ")
    List<String> jpqlGetPnsByModelNameAndType(@Param("type") String type, @Param("type_item") String typeItem);

    @Query("SELECT bpT.pn AS pn " +
            "FROM AgileBomPn AS bpT ")
    List<String> jpqlGetPns();

    @Query("SELECT DISTINCT rwr.pNo AS p_no " +
            "FROM RWoRequest rwr " +
            "WHERE rwr.build LIKE '%B04%' " +
            "AND rwr.pNo NOT IN ('40-2411-1001') ")
    List<String> jpqlGetListModel();
}
