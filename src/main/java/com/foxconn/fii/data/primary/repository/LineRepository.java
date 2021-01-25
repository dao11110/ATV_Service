package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LineRepository extends JpaRepository<Line, Integer> {
    @Query("SELECT l AS line " +
            "FROM Line AS l " +
            "WHERE l.idGroup IN (105, 106) " +
            "AND l.name LIKE :name ")
    List<Line> jpqlGetLineByNameLine(String name);
}
