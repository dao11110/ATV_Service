package com.amkor.common.repository;

import com.amkor.models.ATVNetMiscTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MiscTableRepository extends JpaRepository<ATVNetMiscTableModel, Long> {

    @Query(value = "SELECT * FROM misc_table WHERE factory_id = ?1 AND table_id = ?2 AND table_code_01 = ?3 AND table_code_02 = ?4", nativeQuery = true)
    List<ATVNetMiscTableModel> getList(int factoryId, String tableId, String tableCode1, String tableCode2);

    @Query(value = "SELECT top 1 * FROM misc_table WHERE factory_id = ?1 AND table_id = ?2 AND table_code_01 = ?3 AND table_code_02 = ?4", nativeQuery = true)
    ATVNetMiscTableModel getOne(int factoryId, String tableId, String tableCode1, String tableCode2);
}
