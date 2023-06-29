package com.wgcloud.entity;

import java.util.Date;
import java.util.List;

public class NetConnetInfo extends BaseEntity{

    private static final long serialVersionUID = 8698619936744959815L;

    private List<NetConnetItem> mList;

    private Date createTime;

    private Boolean xuexi;


    public List<NetConnetItem> getmList() {
        return mList;
    }

    public void setmList(List<NetConnetItem> mList) {
        this.mList = mList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getXuexi() {
        return xuexi;
    }

    public void setXuexi(Boolean xuexi) {
        this.xuexi = xuexi;
    }
}
