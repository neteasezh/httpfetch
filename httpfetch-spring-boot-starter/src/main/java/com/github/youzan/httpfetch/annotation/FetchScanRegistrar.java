package com.github.youzan.httpfetch.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhuhai
 * @date 2023/10/26
 */
public class FetchScanRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {
    Logger logger = LoggerFactory.getLogger(FetchScanRegistrar.class);

    private BeanFactory beanFactory;
    private static String basePackage;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes fetchScanAttrs = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(FetchScan.class.getName()));
        List<String> basePackages = new ArrayList<>();

        if (fetchScanAttrs != null) {
            basePackages.addAll(Arrays.stream(fetchScanAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
            basePackages.addAll(Arrays.stream(fetchScanAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
            basePackages.addAll(Arrays.stream(fetchScanAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
        }

        if (Objects.isNull(fetchScanAttrs) && StringUtils.isEmpty(basePackage)) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            basePackages.addAll(AutoConfigurationPackages.get(this.beanFactory));
        }
        if (!CollectionUtils.isEmpty(basePackages)) {
            basePackage = StringUtils.collectionToCommaDelimitedString(basePackages);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static String getBasePackage() {
        return basePackage;
    }
}
