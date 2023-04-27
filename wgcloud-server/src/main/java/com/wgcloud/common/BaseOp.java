package com.wgcloud.common;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BaseOp {

    private static String NOW = "${now}";
    private static String NOWHOUR = "${nowhour}";
    private static String NOWHOURY = "${nowhourY}";
    private static String DAYTOHOUR = "${daytohour}";

    private static String TODAY = "${today}";

    private static String TOMORROW="${tomorrow}";
    private static String NHOR="${nhour}";


    private static String LTODAY = "${ltoday}";
    private static String LHOUR = "${lhour}";

    private static String CMONTH="${cmonth}";
    private static String NMONTH="${nmonth}";
    private static String LMONTH="${lmonth}";

    private static String CMONTHL="${cmonthL}";


    private static String T1_TODAY = "${t1_today}";
    private static String T1_TOMORROW="${t1_tomorrow}";
    private static String T1_LTODAY = "${t1_ltoday}";

    private static String NDAYAGO = "${GO";
    private static String NHOURAGOY = "${HY";
    private static String NHOURAGOM = "${HM";

    static SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat dateFormatDay =new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat dateFormatHourEx =new SimpleDateFormat("yyyyMMddHH");
    static SimpleDateFormat dateFormatHour =new SimpleDateFormat("yyyy-MM-dd HH:00:00");
    static SimpleDateFormat dateFormatHourY =new SimpleDateFormat("yyyy/MM/dd HH:00:00");
    static SimpleDateFormat dateFormatMonth =new SimpleDateFormat("yyyy-MM-01");
    static SimpleDateFormat dateFormatMonthL =new SimpleDateFormat("yyyy-MM");

    static SimpleDateFormat mDateFormatDay =new SimpleDateFormat("yyyyMMdd");

    public static class FunInfos{
        String funKey;
        String funName;
        String note;

        public FunInfos(String funKey,String funName,String note){
            this.funKey = funKey;
            this.funName = funName;
            this.note=note;
        }

        public String getFunKey() {
            return funKey;
        }

        public void setFunKey(String funKey) {
            this.funKey = funKey;
        }

        public String getFunName() {
            return funName;
        }

        public void setFunName(String funName) {
            this.funName = funName;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
    private static List<FunInfos>  lFunInfo = null;

    public static List<FunInfos> getFuns(){
        if(lFunInfo == null){
            lFunInfo = new ArrayList<>();
            lFunInfo.add(new FunInfos("${now}","当前时刻：yyyy-MM-dd HH:mm:ss",""));
            lFunInfo.add(new FunInfos("${nowhour}","当前时刻（到小时）：yyyy-MM-dd HH:00:00",""));
            lFunInfo.add(new FunInfos("${nowhourY}","当前时刻(到小时)：yyyy/MM/dd HH:00:00",""));
            lFunInfo.add(new FunInfos("${daytohour}","当前时刻(到小时)：yyyyMMddHH",""));
            lFunInfo.add(new FunInfos("${today}","今天：yyyy-MM-dd",""));
            lFunInfo.add(new FunInfos("${tomorrow}","明天：yyyy-MM-dd",""));
            lFunInfo.add(new FunInfos("${nhour}","下一小时：yyyy-MM-dd HH:00:00",""));

            lFunInfo.add(new FunInfos("${ltoday}","昨天：yyyy-MM-dd",""));
            lFunInfo.add(new FunInfos("${lhour}","上一小时:yyyy-MM-dd HH:00:00",""));

            lFunInfo.add(new FunInfos("${cmonth}","本月：yyyy-MM-01",""));
            lFunInfo.add(new FunInfos("${nmonth}","下月：yyyy-MM-01",""));
            lFunInfo.add(new FunInfos("${lmonth}","上月：yyyy-MM-01",""));
            lFunInfo.add(new FunInfos("${cmonthL}","本月：yyyy-MM",""));

            lFunInfo.add(new FunInfos("${t1_today}","今天：yyyyMMdd",""));
            lFunInfo.add(new FunInfos("${t1_tomorrow}","明天：yyyyMMdd",""));
            lFunInfo.add(new FunInfos("${t1_ltoday}","昨天：yyyyMMdd",""));

            lFunInfo.add(new FunInfos("${GO+-N}","前后N天：yyyy-MM-dd",""));
            lFunInfo.add(new FunInfos("${HM+-N}","前后N小时：yyyy-MM-dd HH:00:00",""));
            lFunInfo.add(new FunInfos("${HY+-N}","前后N小时:yyyy/MM/dd HH:00:00",""));
            lFunInfo.add(new FunInfos("$MD5(加密文本)","加密文本处填入对应字符串",""));

        }
        return lFunInfo;

    }

    public static String replaceVal(String sql){
        if(sql == null || sql.length() < 5)return sql;
        String rsql = sql;
        if(rsql.indexOf("${") < 0){
            return sql;
        }
        Date now =new Date();
        if(rsql.indexOf("${t1_") > 0){
            rsql = rsql.replace(T1_TODAY,mDateFormatDay.format(now));
            rsql = rsql.replace(T1_TOMORROW,mDateFormatDay.format(afterOneDayToNowDate(now)));
            rsql = rsql.replace(T1_LTODAY,mDateFormatDay.format(beforeOneDayToNowDate(now)));
        }
        int posGo =1;
        while(posGo > 0) {
            posGo = rsql.indexOf(NDAYAGO);
            if (posGo > 0) {
                int posEGO = rsql.indexOf('}', posGo);
                if (posEGO > 0) {
                    String val = rsql.substring(posGo + 5, posEGO);
                    Integer v = 0;
                    try {
                        v = Integer.parseInt(val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (rsql.charAt(posGo + 4) == '+') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatDay.format(afterNDayToNowDate(now, v)));
                    } else if (rsql.charAt(posGo + 4) == '-') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatDay.format(beforeNDayToNowDate(now, v)));
                    }
                    else{
                        break;
                    }
                }

            }
        }

        posGo =1;
        while(posGo > 0){
            posGo = rsql.indexOf(NHOURAGOY);
            if (posGo > 0) {
                int posEGO = rsql.indexOf('}', posGo);
                if (posEGO > 0) {
                    String val = rsql.substring(posGo + 5, posEGO);
                    Integer v = 0;
                    try {
                        v = Integer.parseInt(val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (rsql.charAt(posGo + 4) == '+') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatHourY.format(afterOneHourToNowDate(now, v)));
                    } else if (rsql.charAt(posGo + 4) == '-') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatHourY.format(beforeOneHourToNowDate(now, v)));
                    }
                    else{
                        break;
                    }
                }

            }
        }
        posGo =1;
        while(posGo > 0){
            posGo = rsql.indexOf(NHOURAGOM);
            if (posGo > 0) {
                int posEGO = rsql.indexOf('}', posGo);
                if (posEGO > 0) {
                    String val = rsql.substring(posGo + 5, posEGO);
                    Integer v = 0;
                    try {
                        v = Integer.parseInt(val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (rsql.charAt(posGo + 4) == '+') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatHour.format(afterOneHourToNowDate(now, v)));
                    } else if (rsql.charAt(posGo + 4) == '-') {
                        rsql = rsql.replace(rsql.substring(posGo, posEGO + 1), dateFormatHour.format(beforeOneHourToNowDate(now, v)));
                    }
                    else{
                        break;
                    }
                }

            }
        }
        rsql = rsql.replace(NOW,simpleDateFormat.format(now));
        rsql = rsql.replace(NOWHOURY,dateFormatHourY.format(now));
        rsql = rsql.replace(TODAY,dateFormatDay.format(now));
        rsql = rsql.replace(NOWHOUR,dateFormatHour.format(now));


        rsql = rsql.replace(NHOR,dateFormatHour.format(afterOneHourToNowDate(now)));
        rsql = rsql.replace(TOMORROW,dateFormatDay.format(afterOneDayToNowDate(now)));

        rsql = rsql.replace(LHOUR,dateFormatHour.format(beforeOneHourToNowDate(now)));
        rsql = rsql.replace(LTODAY,dateFormatDay.format(beforeOneDayToNowDate(now)));
        rsql = rsql.replace(DAYTOHOUR,dateFormatHourEx.format(now));

        rsql = rsql.replace(CMONTH,dateFormatMonth.format((now)));
        rsql = rsql.replace(CMONTHL,dateFormatMonthL.format((now)));
        //有bug，后面改 目前这儿用不上
        rsql = rsql.replace(NMONTH,dateFormatMonth.format(afterOneMonthToNowDate(now)));
        rsql = rsql.replace(LMONTH,dateFormatMonth.format(beforeOneMonthToNowDate(now)));



        return rsql;


    }

    /**
     * 获取当前时间前一小时的时间
     * @param date
     * @return java.util.Date
     */
    public static Date beforeOneHourToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        return calendar.getTime();
    }

    /**
     * 获取当前时间前一小时的时间
     * @param date
     * @return java.util.Date
     */
    public static Date beforeOneHourToNowDate(Date date,int N) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -N);
        return calendar.getTime();
    }

    /**
     * 获取当前时间后小时的时间
     * @param date
     * @return java.util.Date
     */
    public static Date afterOneHourToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return calendar.getTime();
    }
    /**
     * 获取当前时间后小时的时间
     * @param date
     * @return java.util.Date
     */
    public static Date afterOneHourToNowDate(Date date,int N) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, N);
        return calendar.getTime();
    }
    /**
     * 获取当前时间前一一天的时间
     * @param date
     * @return java.util.Date
     */
    public static Date beforeOneDayToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取当前时间前一一天的时间
     * @param date
     * @return java.util.Date
     */
    public static Date beforeNDayToNowDate(Date date,int n) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.DATE, -n);
        return calendar.getTime();
    }

    /**
     * 获取当前时间后一天的时间
     * @param date
     * @return java.util.Date
     */
    public static Date afterOneDayToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);
        return calendar.getTime();
    }
    /**
     * 获取当前时间后一天的时间
     * @param date
     * @return java.util.Date
     */
    public static Date afterNDayToNowDate(Date date,int N) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.DATE, N);
        return calendar.getTime();
    }
    /**
     * 获取当前时间前一月的时间
     * @param date
     * @return java.util.Date
     */
    public static Date beforeOneMonthToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 获取当前时间后一月的时间
     * @param date
     * @return java.util.Date
     */
    public static Date afterOneMonthToNowDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.setTime(date);
        calendar.add(calendar.MONTH, 1);
        return calendar.getTime();
    }

    public static String[]  splitColor(String colors){

        List<String> arrary = new ArrayList<>();
        while(true) {
            Integer pos = null;
            Integer pos1 = colors.indexOf(",#");
            Integer pos2 = colors.indexOf(",rgb");
            if (pos1 < 1) {
                if (pos2 < 1) {
                    arrary.add(colors);
                    String[] ret = new String[arrary.size()];
                    arrary.toArray(ret);
                    return ret;
                } else {
                    pos = pos2;

                }
            } else {
                if (pos2 > 1) {
                    pos = Math.min(pos1, pos2);
                } else {
                    pos = pos1;

                }
            }
            String c1 = colors.substring(0, pos);
            String c2 = colors.substring(pos+1, colors.length());
            arrary.add(c1);
            colors = c2;
        }
    }
    public static String FormatValue(String format,Object value){
        if(value == null)return null;
        if(format == null || format.length() < 1)return  value.toString();
        DecimalFormat dFormat = new DecimalFormat(format.replace(",",""));
        try {
            return dFormat.format(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  value.toString();
    }

    public static String FormatX(String format,Object value){
        if(format == null || format.length() < 1)return value.toString();
        if( value instanceof java.sql.Date || value instanceof Timestamp){
            //日期格式
            SimpleDateFormat dFormat =new SimpleDateFormat(format);
            return dFormat.format(value);
        }
        return value.toString();
    }


    public static void main(String[] args) {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey deskey = keygen.generateKey();
        System.out.println(Base64.getEncoder().encode(deskey.getEncoded()));
    }

    public static String getRplSqlVal(String val){
        if(val == null)return val;
        val = val.replace("'","");
        val = val.replace("\"","");
        val = val.replace(" ","");
        val = val.replace(",","");
        val = val.replace("\t","");
        return val;
    }


}
