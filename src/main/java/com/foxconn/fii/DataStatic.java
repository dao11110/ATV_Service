package com.foxconn.fii;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class DataStatic {
    public static final String URL_PART_IMAGE = "/gsiot/ws-data/images";

    public static class Status{
        public static Integer SUCCESS = 1;
        public static Integer FAIL = 2;
    }

    public static class ITSFC{
        public static class B04{
            public static String GET_SOLDER = "http://10.224.81.86/GET_API/api/getsolder";
            public static String GET_TIME_FAI = "http://10.224.81.86/get_api/api/getdata";
        }
    }

    public static String deAccent(String mStr){
        String nfd = Normalizer.normalize(mStr, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCOMBINING_DIACRITICAL_MARKS}+");
        return pattern.matcher(nfd).replaceAll("");
    }

    public static class FILE{
        public static String SUB_FOLDER(Date mDate){
            Calendar time = Calendar.getInstance();
            time.setTime(mDate);
            Integer year = time.get(Calendar.YEAR);
            Integer month = time.get(Calendar.MONTH) + 1;
            Integer day = time.get(Calendar.DAY_OF_MONTH);

            return year+"/"+month+"/"+day+"/";
        }
    }
}
