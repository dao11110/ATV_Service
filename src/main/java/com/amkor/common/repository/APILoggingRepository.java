package com.amkor.common.repository;

import com.amkor.models.ATVNetAPILoggingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APILoggingRepository extends JpaRepository<ATVNetAPILoggingModel, Long> {
}
