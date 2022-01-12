package com.wgcloud.util;

import java.util.Random;
import java.util.UUID;


/**
 * @version v2.3
 * @ClassName:UUIDUtil.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: UUIDUtil.java
 *
 */
public class UUIDUtil {

    public static String getUUID() {
        return String.valueOf(UUID.randomUUID()).replace("-", "");
    }

    /**
     * 随机6位数字
     *
     * @return
     */
    public static String getRandomSix() {
        return "" + new Random().nextInt(999999);
    }

}
