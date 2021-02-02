package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.RWoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RWoRequestRepository extends JpaRepository<RWoRequest, Integer> {
    @Query("SELECT MAX(rwr.downloadTime) AS time " +
            "FROM RWoRequest AS rwr " +
            "WHERE rwr.wo LIKE :wo ")
    List<Date> jpqlCheckTimeDownloadBomByWo(@Param("wo") String wo);
}
