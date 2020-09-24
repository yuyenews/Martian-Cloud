package com.mars.cloud.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarsFeign {
    /**
     * 服务名称
     * @return 服务名称
     */
    String serverName();

    /**
     * beanName
     * @return
     */
    String beanName() default "";
}
