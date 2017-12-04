package com.free.dquery.annotation;

import java.lang.annotation.*;

/**
 * @author zhangzhidong
 * @date 2017/11/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Documented
public @interface DynamicSql {

    /**
     * 需要动态拼凑的sql
     *
     * @return
     */
    String sql() default "";

    /**
     * 判断SQL是否要拼接的条件表达式语句 默认不拼接
     * <p>表达式: 取@Param 注解对象直接按照逻辑运算<p/>
     * <p>表达式为 true时拼接, false不拼接</p>
     * <li>如: dto.name != null && dto.name !=''</li>
     * <li>   dto.id != null && dto.name >=0 </li>
     * <li>   list.length > 0 </li>
     * <p>注意: 数字类型 若提前不判断null的话,默认值为0</p>
     *
     * @return
     */
    String conditions() default "";
}
