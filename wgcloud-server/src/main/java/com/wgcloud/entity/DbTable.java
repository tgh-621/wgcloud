package com.wgcloud.entity;

import java.sql.Timestamp;

/**
 * @version v2.3
 * @ClassName:DbTable.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: 检查系统入侵信息
 * @Copyright: 2017-2021 wgcloud. All rights reserved.
 */
public class DbTable extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = 879979812204191283L;


    /**
     * 数据源
     */
    private String dbInfoId;


    /**
     * 表名称
     */
    private String tableName;

    /**
     * where条件值
     */
    private String whereVal;

    /**
     * 表别名
     */
    private String remark;

    private Long tableCount;

    private Long value;

    private String dateStr;

    private Integer warnCountL;
    private Integer warnCountH;
    private Integer warnCount;
    private String sql;
    private String fSql;

    private String warnEmail;

    private Integer inUse;


    /**
     * 创建时间
     */
    private Timestamp createTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getWhereVal() {
        return whereVal;
    }

    public void setWhereVal(String whereVal) {
        this.whereVal = whereVal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTableCount() {
        return tableCount;
    }

    public void setTableCount(Long tableCount) {
        this.tableCount = tableCount;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getValue() {
        return tableCount;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getDbInfoId() {
        return dbInfoId;
    }

    public void setDbInfoId(String dbInfoId) {
        this.dbInfoId = dbInfoId;
    }

    public Integer getWarnCountL() {
        return warnCountL;
    }

    public void setWarnCountL(Integer warnCountL) {
        this.warnCountL = warnCountL;
    }

    public Integer getWarnCountH() {
        return warnCountH;
    }

    public void setWarnCountH(Integer warnCountH) {
        this.warnCountH = warnCountH;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer getInUse() {
        return inUse;
    }

    public void setInUse(Integer inUse) {
        this.inUse = inUse;
    }

    public String getWarnEmail() {
        return warnEmail;
    }

    public void setWarnEmail(String warnEmail) {
        this.warnEmail = warnEmail;
    }

    public Integer getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(Integer warnCount) {
        this.warnCount = warnCount;
    }

    public String getfSql() {
        return fSql;
    }

    public void setfSql(String fSql) {
        this.fSql = fSql;
    }
}
