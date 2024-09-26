package com.github.youzan.httpfetch.autoconfigure;

import com.github.youzan.httpfetch.annotation.FetchScanRegistrar;
import com.github.youzan.httpfetch.http.HttpApiService;
import com.github.youzan.httpfetch.spring.HttpApiClassPathBeanDefinitionScanner;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/10/31
 */
public class HttpFetchBeanRegister implements ResourceLoaderAware, BeanPostProcessor {
    private ResourceLoader resourceLoader;
    private ConfigurableApplicationContext applicationContext;
    private HttpApiService httpApiService;

    public HttpFetchBeanRegister(ConfigurableApplicationContext applicationContext, HttpApiService httpApiService) {
        this.applicationContext = applicationContext;
        this.httpApiService = httpApiService;
    }

    public void registerScanner() {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        HttpApiClassPathBeanDefinitionScanner scanner = new HttpApiClassPathBeanDefinitionScanner(registry, httpApiService);
        if (Objects.nonNull(resourceLoader)) {
            scanner.setResourceLoader(resourceLoader);
        }

        String basePackage = FetchScanRegistrar.getBasePackage();
        if (StringUtils.hasText(basePackage)) {
            scanner.register();
            scanner.doScan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
