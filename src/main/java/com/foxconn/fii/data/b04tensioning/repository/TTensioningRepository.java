package com.foxconn.fii.data.b04tensioning.repository;

import com.foxconn.fii.data.b04tensioning.model.TTensioning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface TTensioningRepository extends JpaRepository<TTensioning, Integer> {
    @Query(value = "select tt.* from TTensioning tt\n" +
            "\twhere Testtime= (select max(Testtime) from TTensioning where Flag_Type=:flagType)",nativeQuery = true)
    List<Map<String,Object>> jpqlGetTTensioning(@Param("flagType") Integer flagType);

}
