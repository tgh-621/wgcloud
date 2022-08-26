package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:HeathMonitor.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: app端口信息
 *
 */
public class GroupInfo extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111614773445949L;


    /**
     * 应用服务名称
     */
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
