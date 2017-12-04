package com.free.dquery.handle;

import com.free.dquery.annotation.DQuery;
import com.free.dquery.annotation.DynamicSql;
import com.free.dquery.exception.DQueryException;
import com.free.dquery.queryparam.QueryParam;
import com.free.dquery.queryparam.QueryParamList;
import com.free.dquery.util.PageInfo;
import com.free.dquery.util.PageResult;
import com.free.dquery.util.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.SessionFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.util.CollectionUtils;

import javax.script.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author zhangzhidong
 * @date 2017/11/27
 */
public class DQueryHandler {

    // 访问session
    private SessionFactory sessionFactory;
    // 动态查询
    private DQuery dQuery;
    // 切点方法
    private Method method;
    // 分页信息
    private PageInfo pageInfo;

    private ScriptEngine engine;

    public DQueryHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Object handler(ProceedingJoinPoint pjp) throws Throwable {

        // 检查是否需要拦截
        if (!checkMethodIsNeedInterception(pjp)) {
            return pjp.proceed();
        }

        // 获取参数
        Map<String, Object> methodParameters = this.getMethodParameters(pjp.getArgs());

        // 逻辑表达式对应的字段值
        this.judgementValues(methodParameters);

        // 获取返回值类型
        Class<?> returnType = method.getReturnType();
        // 获取返回值中
//        Type genericReturnType = method.getGenericReturnType();

        // 获取SQL
        List queryParameters = this.queryParameters(methodParameters);

        String sql = getSql();

        return query(queryParameters, sql, returnType);

    }

    /**
     * @param queryParameters
     * @param sql
     * @param returnType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object query(List queryParameters, String sql, Class<?> returnType) throws InstantiationException, IllegalAccessException {

        if (returnType.isArray() || Collection.class.isAssignableFrom(returnType)) {
            // 列表
            return QueryUtil.queryForList(sql, queryParameters, null, null, sessionFactory);
        } else if (returnType == PageResult.class) {
            // 分页
            List list = QueryUtil.queryForList(sql, queryParameters, pageInfo.getPage(), pageInfo.getSize(), sessionFactory);
            Long total = QueryUtil.queryCountSize(sql, queryParameters, sessionFactory);
            return new PageResult(pageInfo.getPage(), pageInfo.getSize(), total, list);
        } else {
            // 对象javabean
            return QueryUtil.queryForObject(sql, queryParameters, returnType, sessionFactory);
        }


    }

    /**
     * 检查是否需要拦截--同时设置method对象
     *
     * @param pjp
     * @return
     */
    private boolean checkMethodIsNeedInterception(ProceedingJoinPoint pjp) {
        // 获取接口
        Signature sign = pjp.getSignature();
        MethodSignature ms = (MethodSignature) sign;
        this.method = ms.getMethod();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            // 含有自定义的注解
            if (annotation instanceof DQuery) {
                dQuery = (DQuery) annotation;
                return true;
            }
        }
        return false;
    }

    /**
     * 获取参数值
     *
     * @param args
     * @return
     * @throws Exception
     */
    private Map<String, Object> getMethodParameters(Object[] args) throws Exception {
        Map map = new HashMap();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (args.length != parameterAnnotations.length) {
            throw new DQueryException("形参定义的@param 数量,和实参数量不一致");
        }

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;

                    map.put(param.value(), args[i]);

                    if (args[i] instanceof PageInfo) {
                        pageInfo = (PageInfo) args[i];
                    }
                }
            }
        }

        return map;
    }

    /**
     * 查询SQL
     *
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ScriptException
     */
    private String getSql() throws NoSuchFieldException, IllegalAccessException, ScriptException {
        StringBuilder sb = new StringBuilder();
        String sqlHead = dQuery.sqlHead().replace("select", "SELECT").replace("from", "FROM");

        if (StringUtils.isBlank(sqlHead)) {
            throw new DQueryException("哦豁,SQL头部为空,查毛线呢,(提示:神说,遇到新的注解,先看看注释)");
        }
        sb.append(sqlHead);

        // 动态SQL语句
        String isAddSql;
        // 动态SQL是否添加逻辑表达式
        String conditions;
        //  判断逻辑表达式结果
        Boolean flag;
        // 动态添加
        DynamicSql[] dynamicSqls = dQuery.dynamicSql();
        for (DynamicSql dynamicSql : dynamicSqls) {
            isAddSql = dynamicSql.sql();
            conditions = dynamicSql.conditions();
            if (StringUtils.isNotBlank(isAddSql) && StringUtils.isNotBlank(conditions)) {
                flag = (Boolean) engine.eval(conditions);
                if (flag) {
                    sb.append(isAddSql);
                }
            }
        }

        //加上SQL 尾部
        sb.append(dQuery.sqlTail());

        return sb.toString();
    }

    /**
     * 获取请求参数
     *
     * @param methodParameters
     * @return
     * @throws IllegalAccessException
     */
    private List queryParameters(Map<String, Object> methodParameters) throws IllegalAccessException {
        List parameters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : methodParameters.entrySet()) {

            if (entry.getValue() instanceof Number) {
                parameters.add(new QueryParam(entry.getKey(), entry.getValue()));
            } else if (entry.getValue() instanceof String) {
                parameters.add(new QueryParam(entry.getKey(), entry.getValue()));
            } else {
                Class<?> clazz = entry.getValue().getClass();
                Field[] fields = clazz.getDeclaredFields();
                if (fields != null && fields.length > 0) {
                    for (Field field : fields) {
                        String key = entry.getKey().concat(".").concat(field.getName());
                        field.setAccessible(true);
                        Object target = field.get(entry.getValue());
                        if (null == target || StringUtils.isBlank(target.toString())) {
                            continue;
                        }
                        if (target.getClass().isArray() || Collection.class.isAssignableFrom(target.getClass())) {
                            parameters.add(new QueryParamList(key, ((List) target).toArray()));
                        } else {
                            parameters.add(new QueryParam(key, target));
                        }
                    }
                }
            }

        }
        return parameters;
    }

    /**
     * 判断逻辑表达式对应字段的值
     *
     * @param methodParameters
     */
    private void judgementValues(Map<String, Object> methodParameters) {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("JavaScript");

        if (!CollectionUtils.isEmpty(methodParameters)) {
            for (Map.Entry<String, Object> entry : methodParameters.entrySet()) {
                engine.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
