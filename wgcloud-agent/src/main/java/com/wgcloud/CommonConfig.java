package com.wgcloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;


@Data
@Configuration
@ConfigurationProperties(prefix = "base")
public class CommonConfig {

    private String serverUrl = "";

    private String bindIp = "";

    private String wgToken = "";

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getBindIp() {
        return getIp();
    }
    private static  String wIp = null;
    private String getIp(){
        if(wIp != null)return wIp;
        try {
            String ip =SigarUtil.getMainIp();
            if(ip != null){
                wIp = getHostName()+"_"+ip;
                return wIp;
            }
            else
                return getHostName();
        }catch (Exception e){
            e.printStackTrace();
            return getHostName();
        }
    }

    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }

    public String getWgToken() {
        return wgToken;
    }

    public void setWgToken(String wgToken) {
        this.wgToken = wgToken;
    }

    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (Exception uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }


    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLiunx();
        }
    }

}
