package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:AppInfo.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: app端口信息
 *
 */
public class CmdInfo extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913112613773445949L;


    /**
     * host名称
     */
    private String hostname;

    /**
     * 应用进程ID
     */
    private String id;


    private String cmd;


    private String result;


    private Date execTime;


    /**
     * 创建时间
     */
    private Date createTime;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
