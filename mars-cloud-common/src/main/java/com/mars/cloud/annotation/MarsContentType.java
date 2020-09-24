package com.mars.cloud.annotation;

import com.mars.cloud.annotation.enums.ContentType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarsContentType {

    ContentType ContentType() default ContentType.FORM;
}
