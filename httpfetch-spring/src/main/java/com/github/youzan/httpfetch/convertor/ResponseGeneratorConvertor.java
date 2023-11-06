package com.github.youzan.httpfetch.convertor;


import com.github.youzan.httpfetch.http.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.http.HttpApiRequestParam;

import java.lang.reflect.Method;


/**
 * http响应结果处理接口
 * @author 11047530
 *
 */
public interface ResponseGeneratorConvertor {

	boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, Class<?> responseCls);

	Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, byte[] response, Class<?> responseCls);

}
