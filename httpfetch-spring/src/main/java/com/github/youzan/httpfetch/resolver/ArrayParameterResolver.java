package com.github.youzan.httpfetch.resolver;

import com.github.youzan.httpfetch.annotations.ArrayParam;
import com.github.youzan.httpfetch.http.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.http.HttpApiRequestParam;
import com.github.youzan.httpfetch.http.ParameterWrapper;
import com.github.youzan.httpfetch.http.RequestParameter;
import com.github.youzan.httpfetch.utils.ParameterUtils;

import java.util.Collection;

/**
 * Created by daiqiang on 17/9/7.
 * 数组类型参数的处理
 */
public class ArrayParameterResolver implements MethodParameterResolver {

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        return requestParameter.getParameterWrapper().hasAnnotation(ArrayParam.class);
    }

    @Override
    public boolean resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
        Collection collection = (Collection)requestParameter.getParameter();
        if(!collection.isEmpty()){
            StringBuffer url = new StringBuffer(param.getUrl());
            if(param.getUrl().indexOf("?") < 0 ){
                url.append("?");
            }
            String lastChar = url.substring(url.length()-1, url.length());
            if(!lastChar.equals("?")
                    || !lastChar.equals("&")){
                //如果结尾是?
                url.append("&");
            }
            for(Object e : collection){
                if(e != null){
                    url.append(parameterWrapper.getParamName());
                    url.append("[]=");
                    url.append(ParameterUtils.parseParameter(e));
                    url.append("&");
                }
            }
            param.setUrl(url.toString());
        }

        param.getGetParam().remove(parameterWrapper.getParamName());
        return false;
    }

}
