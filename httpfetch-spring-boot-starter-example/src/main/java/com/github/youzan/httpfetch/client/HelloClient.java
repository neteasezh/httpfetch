package com.github.youzan.httpfetch.client;


import com.github.youzan.httpfetch.annotations.HttpApi;
import com.github.youzan.httpfetch.annotations.QueryParam;

public interface HelloClient {

//    @HttpApi(url = "http://{host}/hello")
    @HttpApi
    String sayHello(@QueryParam("name") String name);

}
