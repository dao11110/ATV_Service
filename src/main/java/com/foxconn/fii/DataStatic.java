package com.foxconn.fii;

import com.foxconn.fii.common.TimeSpan;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class DataStatic {
    public static final String URL_PART_IMAGE = "/gsiot/ws-data/images";

    public static class Status{
        public static Integer SUCCESS = 1;
        public static Integer FAIL = 2;
    }

    public static class MAIL{
        public static String MAIL_SERVER = "http://10.224.81.70:6443/notify-service/api/notify";
        public static final String FOOT_MAIL = "<br>Website: <a href='https://10.224.81.94:6443/paperless/covid-analysis'>https://10.224.81.94:6443/paperless/covid-analysis</a><br><br>This message is automatically sent, please do not reply directly!<br><br>" + "Please contact me by:<br>Mail: cpe-vn-fii-app@mail.foxconn.com<br>Ext: 26143";

    }

    public static class AGILE{
        public static String URL_DOMAIN = " https://vnpdmap.cnsbg.efoxconn.com:10001/";
        public static String URL_DOWNLOAD_BOM = URL_DOMAIN+"exportbomlatest";
        public static String URL_DOWNLOAD_ECN = URL_DOMAIN+"getchange";
        public static String USERNAME = "V0959579";
        public static String PASSWORD = "722crlhUZ~j!";
    }

    public static class ITSFC{
        public static class FACTORY{
            public static String B04 = "B04";
            public static String SERVER_B04 = "http://10.224.81.86/";
            public static String C02 = "C02";
            public static String SERVER_C02 = "http://10.224.81.86/";
            public static String F12 = "F12";
            public static String SERVER_F12 = "http://10.220.81.71/";
        }

        public static class FUNCTION{
            public static final String GET_SOLDER = "GET_API/api/getsolder";
            public static final String GET_ECN_NO = "get_api/api/getecnno";
//            public static final String GET_TIME_FAI = "get_api/api/getdata";
            public static final String GET_MACHINE = "get_api/api/getmachine";
        }

        public static String mapIpServerByFactory(String mFactory){
            Map<String, String> mIps = new HashMap<>();
            mIps.put(FACTORY.B04, FACTORY.SERVER_B04);
            mIps.put(FACTORY.C02, FACTORY.SERVER_C02);
            mIps.put(FACTORY.F12, FACTORY.SERVER_F12);
            return mIps.get(mFactory);
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

    public static TimeSpan getTimeInDay(){
        TimeSpan timeSpan = new TimeSpan();
        Calendar cal = Calendar.getInstance();

        if(cal.get(Calendar.HOUR_OF_DAY) > 7
                || ((cal.get(Calendar.HOUR_OF_DAY) == 7) && (cal.get(Calendar.MINUTE) >= 30))){

            cal.set(Calendar.HOUR_OF_DAY, 7);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);

            timeSpan.setStartDate(cal.getTime());

            cal.set(Calendar.HOUR_OF_DAY, 7);
            cal.set(Calendar.MINUTE, 29);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
            timeSpan.setEndDate(cal.getTime());
        }else{

            cal.set(Calendar.HOUR_OF_DAY, 7);
            cal.set(Calendar.MINUTE, 29);
            cal.set(Calendar.SECOND, 59);
            timeSpan.setEndDate(cal.getTime());

            cal.set(Calendar.HOUR_OF_DAY, 7);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
            timeSpan.setStartDate(cal.getTime());
        }
        //set time start

        return timeSpan;
    }

    public static String getShiftName(){
        Calendar cal = Calendar.getInstance();
        if((cal.get(Calendar.HOUR_OF_DAY) > 7 || ((cal.get(Calendar.HOUR_OF_DAY) == 7) && (cal.get(Calendar.MINUTE) >= 30))) &&
                (cal.get(Calendar.HOUR_OF_DAY) < 19 || ((cal.get(Calendar.HOUR_OF_DAY) == 19) && (cal.get(Calendar.MINUTE) < 30)))){
            return "DAY";
        }else{
            return "NIGHT";
        }
    }

    public static Map<String, List<Map<String, Object>>> convertListToMapList(List<Map<String, Object>> mData, String mKey, boolean isInt){
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        for(int i = 0 ; i < mData.size(); i++){
            String key = "";
            if(isInt){
                key =  String.valueOf((Integer) mData.get(i).get(mKey));
            }else{
                key = ((String) mData.get(i).get(mKey)).trim()+"";
            }
            key = key.toUpperCase();
            if(result.get(key) == null){
                List<Map<String, Object>> tmpList = new ArrayList<>();
                tmpList.add(mData.get(i));
                result.put(key, tmpList);
            }else{
                result.get(key).add(mData.get(i));
            }
        }
        return result;
    }
}
