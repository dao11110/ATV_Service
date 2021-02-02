package com.foxconn.fii.data.b04sfc.repository;

import com.foxconn.fii.data.b04sfc.model.B04RSmtFai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface B04RSmtFaiRepository extends JpaRepository<B04RSmtFai, Integer> {
    @Query(value = "SELECT \"STATION\", \"WO\", \"MODEL_NAME\", \"PROCESS_FLAG\", \"REQUEST_TIME\", \"REASON\", \"CHECK_TIME\"\n" +
            "FROM \"MES4\".\"R_SMT_FAI\"\n" +
            "WHERE \"REQUEST_TIME\" > :time_check ", nativeQuery = true)
    List<Map<String, Object>> findDataMoByTimeMax(@Param("time_check") Date timeCheck);

}
