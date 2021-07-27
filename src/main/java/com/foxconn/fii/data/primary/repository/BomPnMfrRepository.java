package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.agile.AgileBomPnMfr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomPnMfrRepository extends JpaRepository<AgileBomPnMfr, Integer> {

}
