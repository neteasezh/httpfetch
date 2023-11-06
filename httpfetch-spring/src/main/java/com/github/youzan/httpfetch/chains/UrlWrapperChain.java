package com.github.youzan.httpfetch.chains;

import com.github.youzan.httpfetch.annotations.HttpApi;
import com.github.youzan.httpfetch.http.*;
import com.github.youzan.httpfetch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by daiqiang on 17/6/13.
 * 获取url
 */
public class UrlWrapperChain implements HttpApiChain {

    private Logger LOGGER = LoggerFactory.getLogger(UrlWrapperChain.class);

    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private HttpApiConfiguration configuration;

    public UrlWrapperChain(HttpApiConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        //获取url
        String url = getUrl(invocation.getServiceCls(), invocation.getMethod(), invocation.getParameters());
        invocation.getRequestParam().setUrl(url);
        return invoker.invoke(invocation);
    }

    private String getUrl(Class<?> serviceCls, Method method, List<RequestParameter> parameters) {
        HttpApi httpApi = method.getAnnotation(HttpApi.class);
        String forwardUrl = httpApi.url();
        if (CommonUtils.isStringEmpty(forwardUrl)) {
            String code = httpApi.operateCode();
            if (CommonUtils.isStringEmpty(httpApi.operateCode())) {
                //如果为空，则使用默认的操作码
                String beanName = CommonUtils.decapitalize(serviceCls.getSimpleName());
                String methodName = method.getName();
                code = beanName + "." + methodName;
            }
            Object urlObj = configuration.getUrlAlias().get(code);
            if (Objects.isNull(urlObj)) {
                String msg = String.format("url未找到! code [%s]", code);
                throw new RuntimeException(msg);
            }
            forwardUrl = urlObj.toString();
        }
        List<String> routingParameters = obtainRoutingParameters(forwardUrl);
        if (CommonUtils.isCollectionEmpty(routingParameters) || CommonUtils.isCollectionEmpty(parameters)) {
            return forwardUrl;
        }

        Map<String, Object> contextMap = new HashMap<>();
        Map<String, String> paramMap = parameters.stream().collect(Collectors.toMap(r -> r.getParameterWrapper().getParamName(), r -> String.valueOf(r.getParameter())));

        for (String param : routingParameters) {
            String value;
            if (paramMap.containsKey(param)) {
                value = paramMap.get(param);
            } else {
                value = configuration.getEnvironment().getProperty(param);
            }
            contextMap.put(param, value);
        }
        forwardUrl = fillRoutingUrl(contextMap, forwardUrl);
        return forwardUrl;
    }

    /**
     * 填充路由参数
     *
     * @param contextMap
     * @param originUrl
     * @return
     */
    private String fillRoutingUrl(Map<String, Object> contextMap, String originUrl) {
        if (contextMap == null || contextMap.size() == 0) {
            return originUrl;
        }
        String url = originUrl;
        for (Map.Entry<String, Object> entry : contextMap.entrySet()) {
            String key = entry.getKey();
            Object value = contextMap.get(key);
            url = url.replace("{" + key + "}", String.valueOf(value));
        }
        return url;
    }


    /**
     * 获取路由参数
     *
     * @param url
     * @return
     */
    private List<String> obtainRoutingParameters(String url) {
        List<String> routingParameters = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(url);
        while (matcher.find()) {
            routingParameters.add(matcher.group(1));
        }
        return routingParameters;
    }

    @Override
    public int getOrder() {
        return 9000;
    }
}
