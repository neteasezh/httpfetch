package com.github.youzan.httpfetch;


import com.github.youzan.httpfetch.annotation.FetchScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author zhuhai
 * @date 2023/10/27
 */
@FetchScan(basePackages = "com.github.youzan.httpfetch.client")
@SpringBootApplication
public class HttpFetchExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(HttpFetchExampleApplication.class, args);
    }
}
