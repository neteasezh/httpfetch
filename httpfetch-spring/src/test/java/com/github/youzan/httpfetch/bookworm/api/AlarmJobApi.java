package com.github.youzan.httpfetch.bookworm.api;


import com.github.youzan.httpfetch.annotations.Header;
import com.github.youzan.httpfetch.annotations.HttpApi;
import com.github.youzan.httpfetch.annotations.RequestBody;
import java.util.Map;

/**
 * Created by daiqiang on 17/6/16.
 */
public interface AlarmJobApi {

    @HttpApi(method = "POST", headers = @Header(key = "Content-type", value = "application/json"), timeout = 2000, url = "http://alert.s.qima-inc.com/api/v1/alert")
    String alert(@RequestBody Map<String, Object> param);

}
