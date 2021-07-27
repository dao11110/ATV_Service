package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.PaperLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaperLogRepository extends JpaRepository<PaperLog, Integer> {
}
