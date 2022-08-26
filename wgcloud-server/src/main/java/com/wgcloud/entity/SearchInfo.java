package com.wgcloud.entity;

/**
 * @version v2.3
 * @ClassName:HeathMonitor.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: app端口信息
 *
 */
public class SearchInfo extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -291311161477344594L;


    /**
     * 应用服务名称
     */
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
