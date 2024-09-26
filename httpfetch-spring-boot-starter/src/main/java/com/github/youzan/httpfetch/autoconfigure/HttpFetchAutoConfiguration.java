package com.github.youzan.httpfetch.autoconfigure;

import com.github.youzan.httpfetch.annotation.FetchScanRegistrar;
import com.github.youzan.httpfetch.http.HttpApiConfiguration;
import com.github.youzan.httpfetch.http.HttpApiService;
import com.github.youzan.httpfetch.reader.SourceReader;
import com.github.youzan.httpfetch.reader.XmlReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhuhai
 * @date 2023/10/26
 */

@Configuration
@Import(FetchScanRegistrar.class)
@EnableConfigurationProperties(HttpFetchProperties.class)
public class HttpFetchAutoConfiguration {

    private final HttpFetchProperties properties;
    private final List<SourceReader> sourceReaders;
    private final Environment environment;

    private final ConfigurableApplicationContext applicationContext;

    public HttpFetchAutoConfiguration(HttpFetchProperties properties,
                                      ObjectProvider<List<SourceReader>> readersProvider,
                                      Environment environment,
                                      ConfigurableApplicationContext applicationContext) {
        this.properties = properties;
        this.sourceReaders = readersProvider.getIfAvailable();
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    HttpApiService httpApiService() {
        HttpApiConfiguration configuration = new HttpApiConfiguration();
        List<SourceReader> readers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceReaders)) {
            readers.addAll(sourceReaders);
        }
        if (Objects.nonNull(properties) && StringUtils.hasText(properties.getConfigLocation())) {
            XmlReader xmlReader = new XmlReader(properties.getConfigLocation());
            readers.add(xmlReader);
        }
        configuration.setSourceReaders(readers);
        configuration.setEnvironment(environment);
        configuration.init();
        HttpApiService httpApiService = new HttpApiService(configuration);
        httpApiService.init();
        return httpApiService;
    }



    @Bean
    @ConditionalOnMissingBean
    HttpFetchBeanRegister httpFetchBeanRegister(HttpApiService httpApiService) {
        HttpFetchBeanRegister fetchBeanRegister = new HttpFetchBeanRegister(applicationContext, httpApiService);
        fetchBeanRegister.registerScanner();
        return fetchBeanRegister;
    }
}
