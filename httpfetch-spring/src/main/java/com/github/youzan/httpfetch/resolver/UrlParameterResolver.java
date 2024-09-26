package com.github.youzan.httpfetch.resolver;

import com.github.youzan.httpfetch.annotations.URL;
import com.github.youzan.httpfetch.http.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.http.HttpApiRequestParam;
import com.github.youzan.httpfetch.http.RequestParameter;

/**
 * Created by daiqiang on 17/6/19.
 */
public class UrlParameterResolver implements MethodParameterResolver {

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        return requestParameter.getParameterWrapper().hasAnnotation(URL.class) && requestParameter.getParameter() instanceof String;
    }

    @Override
    public boolean resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        param.setUrl(requestParameter.getParameter().toString());
        return false;
    }

}
