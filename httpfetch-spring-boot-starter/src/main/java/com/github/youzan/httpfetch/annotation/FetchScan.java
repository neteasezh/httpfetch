package com.github.youzan.httpfetch.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhuhai
 * @date 2023/10/26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({FetchScanRegistrar.class})
public @interface FetchScan {
    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
