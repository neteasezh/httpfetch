package com.github.youzan.httpfetch.spring;

import com.github.youzan.httpfetch.http.HttpApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import java.util.Set;

/**
 * Created by daiqiang on 17/7/26.
 */
public class HttpApiClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiClassPathBeanDefinitionScanner.class);

    private HttpApiService httpApiService;

    public HttpApiClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, HttpApiService httpApiService) {
        super(registry);
        this.httpApiService = httpApiService;
    }

    public void register(){
        //仅加载HttpApi 注解的class
        addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            // 只支持接口
            return metadataReader.getClassMetadata().isInterface();
        });
    }

    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        for(BeanDefinitionHolder holder : beanDefinitionHolders){
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

            try {
                Class<?> clazz = Class.forName(definition.getBeanClassName());
                definition.getPropertyValues().add("targetClass", clazz);
                definition.getPropertyValues().add("httpApiService", httpApiService);
                definition.setBeanClass(HttpApiFactoryBean.class);
            } catch (ClassNotFoundException e) {
                LOGGER.error("class not found! className [{}]", definition.getBeanClassName(), e);
                throw new RuntimeException("class not found!");
            }
        }
        return beanDefinitionHolders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }

}
