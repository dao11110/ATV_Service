package com.foxconn.fii.data.b04stencil.repository;

import com.foxconn.fii.data.b04stencil.model.TTensioning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;


public interface TTensioningRepository extends JpaRepository<TTensioning, String> {
    @Query(value = "SELECT TOP 1 TTensioning.* \n" +
            "FROM [dbo].[TTensioning]\n" +
            "ORDER BY Testtime DESC", nativeQuery = true)
    Map<String, Object> jpqlGetTopTTensioning();
}
