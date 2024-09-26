package com.github.youzan.httpfetch.spring;

import com.github.youzan.httpfetch.http.HttpApiService;
import org.springframework.beans.factory.FactoryBean;


/**
 * Created by daiqiang on 17/7/26.
 */
public class HttpApiFactoryBean implements FactoryBean {

    private HttpApiService httpApiService;

    private Class<?> targetClass;

    @Override
    public Object getObject() throws Exception {
        return httpApiService.getOrCreateService(targetClass);
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public HttpApiService getHttpApiService() {
        return httpApiService;
    }

    public void setHttpApiService(HttpApiService httpApiService) {
        this.httpApiService = httpApiService;
    }
}
