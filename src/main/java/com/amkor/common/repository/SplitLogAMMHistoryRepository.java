package com.amkor.common.repository;

import com.amkor.models.SplitLogAMMHistoryModel;
import com.amkor.models.VehicleHeaderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitLogAMMHistoryRepository  extends JpaRepository<SplitLogAMMHistoryModel, Integer> {


}
