package com.free.dquery.annotation;

import org.springframework.data.jpa.repository.Query;

import java.lang.annotation.*;

/**
 * @author zhangzhidong
 * @date 2017/11/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@Query(value = "select 1", nativeQuery = true)
public @interface DQuery {

    /**
     * SQL 的头部
     * <p>最终的sql =  SQL头 + 动态SQL + SQL尾</p>
     * <p>这个SQL头能不能为""</p>
     *
     * @return
     */
    String sqlHead() default "";

    /**
     * 动态SQL
     *
     * @return
     */
    DynamicSql[] dynamicSql() default {};

    /**
     * SQL 的尾
     * <p>最终的sql =  SQL头 + 动态SQL + SQL尾</p>
     * <p>这个SQL尾是能为""的</p>
     *
     * @return
     */
    String sqlTail() default "";

}
