package com.github.youzan.httpfetch.convertor;

import com.github.youzan.httpfetch.http.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.http.HttpApiRequestParam;
import com.github.youzan.httpfetch.utils.ByteConvertorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * 默认的参数转换服务
 * @author 11047530
 *
 */
public class DefaultResponseGeneratorConvertor implements ResponseGeneratorConvertor {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultResponseGeneratorConvertor.class);

    @Override
    public Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam requestParam, byte[] response, Class<?> responseCls) {
        return ByteConvertorUtil.byteToObj(method, wrapper, requestParam, response, responseCls);
    }

    @Override
    public boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam requestParam, Class<?> responseCls) {
        return true;
    }

}
