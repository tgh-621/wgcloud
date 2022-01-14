package com.wgcloud.util.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:WarnPools.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: WarnPools.java
 *
 */
public class WarnPools {


    /**
     * 存贮每天发送的内存告警信息map<用户ID+服务器IP，1>
     */
    public static Map<String, Integer> CPU_WARN_MAP = new HashMap<>();
    public static Map<String, Integer> MEM_WARN_MAP = new HashMap<>();
    public static Map<String, Integer> DESK_WARN_MAP = new HashMap<>();
    public static Map<String, Integer> API_WARN_MAP = new HashMap<>();
    public static Map<String, Integer> HOST_WARN_MAP = new HashMap<>();
    public static Map<String, Integer> APP_WARN_MAP = new HashMap<>();

    public static void clearOldData() {
        MEM_WARN_MAP.clear();
        DESK_WARN_MAP.clear();
        CPU_WARN_MAP.clear();
        API_WARN_MAP.clear();
        HOST_WARN_MAP.clear();
        APP_WARN_MAP.clear();
    }

}
