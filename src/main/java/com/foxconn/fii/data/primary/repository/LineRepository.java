package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface LineRepository extends JpaRepository<Line, Integer> {
    @Query("SELECT l AS line " +
            "FROM Line AS l " +
            "WHERE l.idGroup IN (105, 106) " +
            "AND l.name LIKE :name ")
    List<Line> jpqlGetLineByNameLine(String name);

    @Query(value = "SELECT rr.wo, qc.code\n" +
            "\tFROM line l\n" +
            "\tINNER JOIN qr_code qc ON l.id = qc.id_line\n" +
            "\tINNER JOIN qr_code_config qcc ON qc.id = qcc.id_qr_code\n" +
            "\tINNER JOIN result_report rr ON qcc.id = rr.id_qr_code\n" +
//            "\tWHERE l.[group] = 29 \n" +
            "\tWHERE l.id IN (484,485) \n" +
            "\tAND rr.create_at BETWEEN :time_start AND getdate()\n" +
            "\tGROUP BY rr.wo, qc.code", nativeQuery = true)
    List<Map<String, Object>> jpqlGetDataCovid(@Param("time_start") Date timeStart);
}
