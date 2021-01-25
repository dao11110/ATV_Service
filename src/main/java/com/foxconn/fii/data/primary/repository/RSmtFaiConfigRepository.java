package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RSmtFaiConfigRepository extends JpaRepository<RSmtFaiConfig, Integer> {
    @Query("SELECT MAX(rsfc.requestTime) AS max_time " +
            "FROM RSmtFaiConfig AS rsfc ")
    List<Map<String, Object>> getMaxTime();

    @Modifying
    @Transactional
    @Query(value = "UPDATE rsfc " +
            "SET rsfc.id_qc = mqs.id_qc " +
            "FROM r_smt_fai_config AS rsfc " +
            "INNER JOIN map_qc_sfc AS mqs ON rsfc.station = mqs.station " +
            "WHERE rsfc.id IN :ids", nativeQuery = true)
    int jpqlUpdateIdQcBeforeInsertByIds(@Param("ids") List<Integer> id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE rsfc " +
            "SET rsfc.materials = :materials " +
            ", rsfc.material_fill = :material_fill" +
            ", rsfc.ecn_no = :ecn_no " +
            "FROM r_smt_fai_config AS rsfc " +
            "WHERE rsfc.wo LIKE :wo ", nativeQuery = true)
    int jpqlUpdateDataSolder(@Param("wo") String wo, @Param("materials") String materials, @Param("material_fill") String materialFill, @Param("ecn_no") String ecnNo);

    @Query(value = "SELECT media, create_at\n" +
            "FROM result_report_step\n" +
            "WHERE LEN(media) > 2 \n" +
            "AND create_at BETWEEN :time_start AND :time_end ", nativeQuery = true)
    List<Map<String, Object>> jpqlGetListMedia(@Param("time_start") Date timeStart, @Param("time_end") Date timeEnd);

    @Query(value = "SELECT media, create_at\n" +
            "FROM report_step_history\n" +
            "WHERE LEN(media) > 2 AND media NOT LIKE '%/%'", nativeQuery = true)
    List<Map<String, Object>> jpqlGetListMediaHistory();

    @Query(value = "SELECT DISTINCT wo\n" +
            "FROM r_smt_fai_config", nativeQuery = true)
    List<String> jpqlGetListWo();
}
