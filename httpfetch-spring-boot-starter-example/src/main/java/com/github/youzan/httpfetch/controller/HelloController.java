package com.github.youzan.httpfetch.controller;


import com.github.youzan.httpfetch.client.HelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhai
 * @date 2023/10/27
 */
@RestController
public class HelloController {

    @Autowired
    private HelloClient helloClient;



    @GetMapping("/hello")
    public String hello(String name) {
        return helloClient.sayHello(name);
    }
}
