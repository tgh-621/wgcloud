package com.wgcloud.entity;

import java.util.Date;

public class NetState extends BaseEntity{

    private static final long serialVersionUID = -1412477355088780549L;

    private  String bip;
    private  int bport;
    private  int br;
    private Date tm;


    public String getBip() {
        return bip;
    }

    public void setBip(String bip) {
        this.bip = bip;
    }

    public int getBport() {
        return bport;
    }

    public void setBport(int bport) {
        this.bport = bport;
    }

    public int getBr() {
        return br;
    }

    public void setBr(int br) {
        this.br = br;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }
}
