package com.free.dquery.annotation;

import com.free.dquery.denum.DynamicSqlJudgmentType;

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
     * 根据哪个字段判断此SQL是否需要
     * <p>如果这里为"",SQL 默认不会拼接</p>
     * <p>如果这里为#{obj.o} 则会取传入的obj参数的o属性,也就是mybatis写法,但是sql里面还是hibernate写法,:xxxx ,暂时不支持? 因为,我觉得,这是一种很不负责的写法</p>
     * <li>sql = " and obj.xxx = :simple",judgementField="#{obj.simpl}"</li>
     *
     * @return
     */
    String judgementField() default "";

    /**
     * 当判断字段为啥时,要动态SQL
     * <p>是要</p>
     * <p>要!!!!!</p>
     * <p>  要!!!</p>
     * <p>    要!</p>
     *
     * @return
     */
    DynamicSqlJudgmentType type() default DynamicSqlJudgmentType.NOTNULL;
}
