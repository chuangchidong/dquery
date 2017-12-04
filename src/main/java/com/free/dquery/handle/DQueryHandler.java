package com.free.dquery.handle;

import com.free.dquery.annotation.DQuery;
import com.free.dquery.annotation.DynamicSql;
import com.free.dquery.denum.DynamicSqlJudgmentType;
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

        // 获取返回值类型
        Class<?> returnType = method.getReturnType();
        // 获取返回值中
//        Type genericReturnType = method.getGenericReturnType();

        // 获取SQL
        List queryParameters = this.queryParameters(methodParameters);

        String sql = getSql(methodParameters, queryParameters);

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

    private String getSql(Map methodParameters, List queryParameters) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        String sqlHead = dQuery.sqlHead().replace("select", "SELECT").replace("from", "FROM");
        ;

        if (StringUtils.isBlank(sqlHead)) {
            throw new DQueryException("哦豁,SQL头部为空,查毛线呢,(提示:神说,遇到新的注解,先看看注释)");
        }
        sb.append(sqlHead);

        // 动态添加
        DynamicSql[] dynamicSqls = dQuery.dynamicSql();
        for (DynamicSql dynamicSql : dynamicSqls) {
            String isAddSql = dynamicSql.sql();
            if (StringUtils.isBlank(isAddSql) || StringUtils.isBlank(dynamicSql.judgementField())) {
                continue;
            }
            boolean checkAndPackDynamicSql = checkAndPackDynamicSql(dynamicSql.judgementField(), dynamicSql.type(), methodParameters, queryParameters);
            if (checkAndPackDynamicSql) {
                sb.append(isAddSql);
            }
        }

        //加上SQL 尾部
        sb.append(dQuery.sqlTail());

        return sb.toString();
    }

    /**
     * 检查并封装  动态sql
     *
     * @param judgementField    检查字段
     * @param type             判断类型
     * @param methodParameters 所有参数
     * @param queryParameters  查询参数
     * @return
     */
    private boolean checkAndPackDynamicSql(String judgementField, DynamicSqlJudgmentType type, Map methodParameters, List queryParameters) throws NoSuchFieldException, IllegalAccessException {
        boolean check = false;
        Object value = null;
        if (!CollectionUtils.isEmpty(queryParameters)) {
            for (Object object : queryParameters) {
                if (object instanceof QueryParam) {
                    if (((QueryParam) object).getKey().equals(judgementField)) {
                        value = ((QueryParam) object).getValue();
                    }
                } else if (object instanceof QueryParamList) {
                    if (((QueryParam) object).getKey().equals(judgementField)) {
                        value = ((QueryParam) object).getValue();
                    }
                }
            }

        }

        switch (type) {
            case NOTNULL:
                if (value != null) {
                    check = true;
                }
                break;
            case NOTEMPTY:
                if (value != null && !StringUtils.isEmpty(value.toString())) {
                    check = true;
                }
                break;
        }
        return check;
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

}
