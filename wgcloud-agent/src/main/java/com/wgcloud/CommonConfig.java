package com.wgcloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;


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
        return getHostName();
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
        } catch (UnknownHostException uhe) {
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
