package com.github.youzan.httpfetch.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;


/**
 * @author zhuhai
 * @date 2023/10/27
 */

@ConfigurationProperties(
        prefix = "http-fetch",
        ignoreUnknownFields = true
)
public class HttpFetchProperties {

    private String configLocation;

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }
}
