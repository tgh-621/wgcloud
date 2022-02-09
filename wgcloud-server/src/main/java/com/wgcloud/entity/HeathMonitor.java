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
public class HeathMonitor extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111613773445949L;


    /**
     * 应用服务名称
     */
    private String appName;

    /**
     * 心跳检测Url
     */
    private String heathUrl;

    /**
     * 状态
     */
    private String heathStatus;


    /**
     * 创建时间
     */
    private Date createTime;


    //头参数
    private String headerParam;

    //请求参数
    private String requestParam;

    //请求结果
    private String lastResult;

    //判断脚本
    private String testScript;

    //判断结果
    private Integer lastStatus;

    //前置任务　
    private String frontId;

    //上次执行时间
    private Date lastTime;

    //参数类型
    private String paramType;

    //请求类型
    private String requestType;

    //cookie
    private String cookieInfo;

    private Integer exeState = 0;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHeathUrl() {
        return heathUrl;
    }

    public void setHeathUrl(String heathUrl) {
        this.heathUrl = heathUrl;
    }

    public String getHeathStatus() {
        return heathStatus;
    }

    public void setHeathStatus(String heathStatus) {
        this.heathStatus = heathStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getHeaderParam() {
        return headerParam;
    }

    public void setHeaderParam(String headerParam) {
        this.headerParam = headerParam;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getLastResult() {
        return lastResult;
    }

    public void setLastResult(String lastResult) {
        this.lastResult = lastResult;
    }

    public String getTestScript() {
        return testScript;
    }

    public void setTestScript(String testScript) {
        this.testScript = testScript;
    }

    public Integer getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Integer lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getFrontId() {
        return frontId;
    }

    public void setFrontId(String frontId) {
        this.frontId = frontId;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getCookieInfo() {
        return cookieInfo;
    }

    public void setCookieInfo(String cookieInfo) {
        this.cookieInfo = cookieInfo;
    }

    public Integer getExeState() {
        return exeState;
    }

    public void setExeState(Integer exeState) {
        this.exeState = exeState;
    }
}
