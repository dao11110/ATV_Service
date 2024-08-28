package com.amkor.service.iService;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public interface IService {

    default String getDriver() {
        return "com.ibm.as400.access.AS400JDBCDriver";
    }

    default String getURL(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "jdbc:as400://10.101.6.12";
                break;
            case "ATV":
                result = "jdbc:as400://10.201.6.21";
                break;
        }
        return result;
    }

    String getUserID(String site);

    String getPasswd(String site);

    default String getPPOMSTP(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "PPSLIB.PPOMSTP";
                break;
            case "ATV":
                result = "EMLIB.PPOMSTP";
                break;
        }
        return result;
    }

    default String getLibrary(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "PPSLIB";
                break;
            case "ATV":
                result = "EMLIB";
                break;
        }
        return result;
    }

    default String getFactoryID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "1";
                break;
            case "ATV":
                result = "80";
                break;
        }
        return result;
    }

    default String getSiteID(String site) {
        String result = "";

        switch (site) {
            case "ATK":
                result = "1";
                break;
            case "ATV":
                result = "1";
                break;
        }
        return result;
    }


    default long getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String strDate = sdf.format(now);
        return Long.parseLong(strDate);
    }

    default long getDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(d);
        return Long.parseLong(strDate);
    }

    default long getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String strDate = sdf.format(now);
        return Long.parseLong(strDate);
    }

    default long getDateTime(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(d);
        return Long.parseLong(strDate);
    }

    default long get400CurrentDate() {
        String current = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return Long.parseLong(current) - 19000000000000L;
    }
}
