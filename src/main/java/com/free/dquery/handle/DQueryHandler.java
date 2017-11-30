package com.free.dquery.handle;

import com.alibaba.fastjson.JSON;
import com.free.dquery.annotation.DQuery;
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
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author zhangzhidong
 * @date 2017/11/27
 */
public class DQueryHandler {

    // 访问session
    private SessionFactory sessionFactory;
    // 访问session
    private DQuery dQuery;
    private Method method;
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
        Type genericReturnType = method.getGenericReturnType();

        System.out.println("methodParameters===" + JSON.toJSONString(methodParameters));

        // 获取SQL
        List queryParameters = this.queryParameters(methodParameters);

        String sql = getSql(methodParameters, queryParameters);
        System.out.println("queryParameters===" + JSON.toJSONString(queryParameters));


        // 查询结果
//        if (returnType.isArray() || Collection.class.isAssignableFrom(returnType)) {
//            return query(queryParameters, sql, true,returnType);
//        } else {
//            return query(queryParameters, sql, false, returnType);
//        }
        return query(queryParameters, sql, returnType);

    }

    /**
     *
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
        } else if (returnType == PageResult.class){
            // 分页
            return QueryUtil.queryForList(sql, queryParameters, pageInfo.getPage(), pageInfo.getSize(), sessionFactory);
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
        System.out.println("实参定义的==" + JSON.toJSONString(args));
        System.out.println("形参定义的==" + JSON.toJSONString(parameterAnnotations));
        for (int i = 0; i < parameterAnnotations.length; i++) {
            System.out.println("形参定义的" + i + "==" + JSON.toJSONString(parameterAnnotations[i]));
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    System.out.println("形参定义的" + i + " param ==" + JSON.toJSONString(parameterAnnotations[i]) + " 实际值" + args[i]);

                    map.put(param.value(), args[i]);
                    // 写死了,判断分页情况
                    Class<?> clazz = Class.forName(args[i].getClass().getTypeName());
                    if (clazz.newInstance() instanceof PageInfo) {
                        pageInfo = (PageInfo) args[i];
                    }
                }
            }
        }

        return map;
    }

    private String getSql(Map methodParameters, List queryParameters) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        String sqlHead = dQuery.sqlHead();
        if (StringUtils.isBlank(sqlHead)) {
            throw new DQueryException("哦豁,SQL头部为空,查毛线呢,(提示:神说,遇到新的注解,先看看注释)");
        }
        sb.append(sqlHead);

        //加上SQL 尾部
        sb.append(dQuery.sqlTail());

        return sb.toString();
    }

    private List queryParameters(Map<String, Object> methodParameters) throws IllegalAccessException {
        List parameters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : methodParameters.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
//            System.out.println("key= " + entry.getKey() + " and value= "
//                    + entry.getValue());


            if (entry.getValue() instanceof Number) {
                parameters.add(new QueryParam(entry.getKey(), entry.getValue()));
            } else if (entry.getValue() instanceof String) {
                parameters.add(new QueryParam(entry.getKey(), entry.getValue()));
            } else {
                Class<?> clazz = entry.getValue().getClass();
                System.out.println("clazz name "+ clazz.getName());
                Field[] fields = clazz.getDeclaredFields();
                if (fields != null && fields.length > 0) {
                    for (Field field : fields) {
                        System.out.println("===="+field.getName());
                        String key = entry.getKey().concat(".").concat(field.getName());
                        field.setAccessible(true);
                        Object target = field.get(entry.getValue());
                        System.out.println("key=="+key);
                        System.out.println("target=="+JSON.toJSONString(target));
                        System.out.println("entry.getValue()=="+JSON.toJSONString(entry.getValue()));
                        if (null == target || StringUtils.isBlank(target.toString())) {
                            continue;
                        }
                        if (target.getClass().isArray() || Collection.class.isAssignableFrom(target.getClass())) {
                            parameters.add(new QueryParamList(key,((List) target).toArray()));
                        }else {
                            parameters.add(new QueryParam(key, target));
                        }
                    }
                }
            }

        }

        return parameters;
    }


}
