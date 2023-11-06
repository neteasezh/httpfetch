package com.github.youzan.httpfetch.annotations;

import java.lang.annotation.*;

/**
 * Created by daiqiang on 16/12/6.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
