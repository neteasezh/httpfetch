package com.github.youzan.httpfetch.annotations;

import java.lang.annotation.*;

/**
 * @author: gongfangping
 * Create in 2018/2/12
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonPath {

    //json路径
    String value();
    
}
