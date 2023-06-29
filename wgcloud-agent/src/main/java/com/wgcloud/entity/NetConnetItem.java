package com.wgcloud.entity;

public class NetConnetItem {

    private  Long localPort = 0L;
    private String localAddress = null;
    private Long remotePort = 0L;
    private String remoteAddress = null;

    public Long getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Long localPort) {
        this.localPort = localPort;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public Long getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Long remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
