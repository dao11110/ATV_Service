package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.RWoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RWoRequestRepository extends JpaRepository<RWoRequest, Integer> {
    @Query("SELECT MAX(rwr.downloadTime) " +
            "FROM RWoRequest AS rwr " +
            "WHERE rwr.wo LIKE :wo " +
            "GROUP BY rwr.wo ")
    List<Date> jpqlCheckTimeDownloadBomByWo(@Param("wo") String wo);

    @Query("SELECT rwr.custKpNo " +
            "FROM RWoRequest AS rwr " +
            "WHERE rwr.wo LIKE :wo ")
    List<String> jpqlGetPnsByWo(@Param("wo") String wo);

    @Query("SELECT MAX(rwr.downloadTime) AS max_time, rwr.build AS build " +
            "FROM RWoRequest rwr " +
            "WHERE rwr.build LIKE :build " +
            "GROUP BY rwr.build ")
    List<Map<String, Object>> jpqlGetMaxTimeDonwload(@Param("build") String build);
}
