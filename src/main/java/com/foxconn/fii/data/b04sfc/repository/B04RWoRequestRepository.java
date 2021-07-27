package com.foxconn.fii.data.b04sfc.repository;

import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface B04RWoRequestRepository extends JpaRepository<B04RWoRequest, Integer> {
    @Query(value = "SELECT \"WO\"\n" +
            "\t\t,\"WO_QTY\"\n" +
            "\t\t,\"P_NO\"\n" +
            "\t\t,\"P_NAME\"\n" +
            "\t\t,\"CUST_KP_NO\"\n" +
            "\t\t,\"STANDARD_QTY\"\n" +
            "\t\t,\"REPLACE_KP_NO\"\n" +
            "\t\t,\"WO_REQUEST\"\n" +
            "\t\t,\"DELIVER_QTY\"\n" +
            "\t\t,\"DOWNLOAD_TIME\"\n" +
            "\t\t,\"CHECKOUT_QTY\"\n" +
            "\t\t,\"RETURN_QTY\"\n" +
            "\t\t,\"ITEM_NO\"\n" +
            "\t\t,\"WASTAGE_QTY\"\n" +
            "\t\t,\"P_VERSION_NEW\"\n" +
            "\t\t,\"KP_VERSION\"\n" +
            "\t\t,\"CUSTOMER_KP_NO\"\n" +
            "\t\t,\"CUSTOMER_KP_NO_VER\"\n" +
            "\t\t,\"RETURN_TEMP\"\n" +
            "\tFROM \"MES4\".\"R_WO_REQUEST\" " +
            "WHERE WO LIKE :wo " +
            "AND DOWNLOAD_TIME > :date ", nativeQuery = true)
    List<B04RWoRequest> jpqlGetPnsByWo(@Param("wo") String wo, @Param("date") Date mDate);

    @Query(value = "SELECT \"WO\"\n" +
            "\t\t,\"WO_QTY\"\n" +
            "\t\t,\"P_NO\"\n" +
            "\t\t,\"P_NAME\"\n" +
            "\t\t,\"CUST_KP_NO\"\n" +
            "\t\t,\"STANDARD_QTY\"\n" +
            "\t\t,\"REPLACE_KP_NO\"\n" +
            "\t\t,\"WO_REQUEST\"\n" +
            "\t\t,\"DELIVER_QTY\"\n" +
            "\t\t,\"DOWNLOAD_TIME\"\n" +
            "\t\t,\"CHECKOUT_QTY\"\n" +
            "\t\t,\"RETURN_QTY\"\n" +
            "\t\t,\"ITEM_NO\"\n" +
            "\t\t,\"WASTAGE_QTY\"\n" +
            "\t\t,\"P_VERSION_NEW\"\n" +
            "\t\t,\"KP_VERSION\"\n" +
            "\t\t,\"CUSTOMER_KP_NO\"\n" +
            "\t\t,\"CUSTOMER_KP_NO_VER\"\n" +
            "\t\t,\"RETURN_TEMP\"\n" +
            "\tFROM \"MES4\".\"R_WO_REQUEST\" " +
            "WHERE DOWNLOAD_TIME > :date ", nativeQuery = true)
    List<B04RWoRequest> jpqlGetPnsByTime(@Param("date") Date mDate);
}
