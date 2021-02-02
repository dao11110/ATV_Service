package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Integer> {
    @Query(value = "SELECT m AS model " +
            "FROM Model AS m " +
            "WHERE m.name LIKE :name")
    List<Model> jpqlGetModelByName(@Param("name") String name);
}
