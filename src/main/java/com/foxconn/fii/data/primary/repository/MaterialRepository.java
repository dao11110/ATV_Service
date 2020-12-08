package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.Material;
import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    @Query("SELECT mT.name AS material_name" +
            ", mT.description AS material_desc" +
            ", mtT.name AS type_name" +
            ", mtT.description AS type_desc " +
            "FROM Material AS mT " +
            "INNER JOIN MaterialType mtT ON mT.idType = mtT.id " +
            "WHERE mT.name LIKE :name_material " +
            "AND mT.idType = 1")
    List<Map<String, Object>> jpqlGetListMaterialRoHS(@Param("name_material") String nameMaterial);
}
