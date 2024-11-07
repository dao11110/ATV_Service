package com.amkor.service.iService;

import com.amkor.common.utils.SharedConstValue;
import com.amkor.models.ApiLoggingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@Service
public interface IWriteService extends IService {
    Logger log = LoggerFactory.getLogger(IWriteService.class);

    default String getUserID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "pruser";
                break;
            case "ATV":
                result = "MESPGMR";
                break;
        }
        return result;
    }

    default String getPasswd(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "prod0504";
                break;
            case "ATV":
                result = "gloryah";
                break;
        }
        return result;
    }


    default int addApiLogging(ApiLoggingModel model) {
        PreparedStatement m_pstmt;
        int record = 0;
        try {
            Class.forName(this.getDriver());
            Connection conn = DriverManager.getConnection(this.getURL(SharedConstValue.AMKOR_SHORTNAME), this.getUserID(SharedConstValue.AMKOR_SHORTNAME), this.getPasswd(SharedConstValue.AMKOR_SHORTNAME));

            String sQuery = "insert into EMLIB.EMESLP04 values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            m_pstmt = conn.prepareStatement(sQuery);
            int i = 1;

            m_pstmt.setInt(i++, model.getCifcid());
            m_pstmt.setInt(i++, model.getCiasid());
            m_pstmt.setString(i++, model.getCistn());
            m_pstmt.setLong(i++, model.getCiamkr());
            m_pstmt.setInt(i++, model.getCisub());
            m_pstmt.setString(i++, model.getCibztp());
            m_pstmt.setString(i++, model.getCists());
            m_pstmt.setFloat(i++, model.getCiseq());
            m_pstmt.setInt(i++, model.getCiopr());
            m_pstmt.setString(i++, model.getCichfd());
            m_pstmt.setString(i++, model.getCiogvl());
            m_pstmt.setString(i++, model.getCinwvl());
            m_pstmt.setString(i++, model.getCirsn());
            m_pstmt.setInt(i++, model.getCichbg());
            m_pstmt.setLong(i++, model.getCichdt());
            m_pstmt.setLong(i++, model.getCirqdt());
            m_pstmt.setString(i++, model.getCirqpg());
            m_pstmt.setInt(i++, model.getCirqbg());
            m_pstmt.setLong(i++, model.getCiacdt());
            m_pstmt.setString(i++, model.getCiacpg());
            m_pstmt.setInt(i, model.getCiacbg());

            record = m_pstmt.executeUpdate();

            m_pstmt.close();
            conn.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return record;
    }
}
