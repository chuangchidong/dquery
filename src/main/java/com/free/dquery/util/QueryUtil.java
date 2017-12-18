package com.free.dquery.util;

import com.free.dquery.queryparam.QueryParam;
import com.free.dquery.queryparam.QueryParamList;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangzhidong
 * @date 2017/11/14
 */
public class QueryUtil {
    // 下划线
    public static final char UNDERLINE = '_';

    /**
     * hibernate 查询返回list
     *
     * @param sql
     * @param param
     * @param page
     * @param size
     * @param clazz
     * @param sessionFactory
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List queryForList(String sql, List<Object> param, Integer page, Integer size, Class<?> clazz, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        Session session = sessionFactory.openSession();
        SQLQuery sqlQuery = setQueryAndParam(sql, param, session);
        if (page != null && size != null) {
            sqlQuery.setFirstResult((page - 1) * size);
            sqlQuery.setMaxResults(page * size);//(这个数为几则要几条)
        }
        List list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        session.close();
        List resultList = queryListToObj(list, clazz);


        return resultList;
    }

    /**
     * hibernate 查询返回对象
     *
     * @param sql
     * @param param
     * @param clazz
     * @param sessionFactory
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object queryForObject(String sql, List<Object> param, Class<?> clazz, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        Session session = sessionFactory.openSession();

        SQLQuery sqlQuery = setQueryAndParam(sql, param, session);

        List list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        session.close();
        Object object;
        List resultList = queryListToObj(list, clazz);
        if (!CollectionUtils.isEmpty(resultList)) {
            object = resultList.get(0);
        } else {
            object = new Object();
        }

        return object;
    }

    /**
     * SQL赋上参数
     *
     * @param sql
     * @param param
     * @param session
     * @return
     */
    private static SQLQuery setQueryAndParam(String sql, List<Object> param, Session session) {
        QueryParam queryParam;
        QueryParamList queryParamList;
        SQLQuery sqlQuery = session.createSQLQuery(sql);

        // 待赋值的字段
        List<String> sqlArr = new ArrayList<>();
        String regEx = ":[a-z]([\\w.?]+)*";
        Pattern pattern = Pattern.compile(regEx);
        Matcher mat = pattern.matcher(sql);
        while (mat.find()) {
            sqlArr.add(mat.group());
        }

        //封装查询条件
        for (Object o : param) {
            if (o instanceof QueryParam) {
                queryParam = (QueryParam) o;
                if (sqlArr.contains(":" + queryParam.getKey())) {
                    sqlQuery.setParameter(queryParam.getKey(), queryParam.getValue());
                }
            }
            if (o instanceof QueryParamList) {
                queryParamList = (QueryParamList) o;
                if (sqlArr.contains(":" + queryParamList.getKey())) {
                    sqlQuery.setParameterList(queryParamList.getKey(), queryParamList.getValue());
                }
            }
        }
        return sqlQuery;
    }

    /**
     * 查询sql 返回行数
     *
     * @param sql
     * @param param
     * @param sessionFactory
     * @return
     */
    public static Long queryCountSize(String sql, List<Object> param, SessionFactory sessionFactory) {

        String sqlCount = StringUtils.replace(sql, sql.substring(sql.indexOf("SELECT"), sql.indexOf("FROM")), "SELECT 1 ");

        sqlCount = "select count(1) as size from (" + sqlCount + ") as t";

        Session session = sessionFactory.openSession();
        SQLQuery sqlQuery = setQueryAndParam(sqlCount, param, session);
        List<Map<String, Object>> list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        session.close();
        return Long.parseLong(list.get(0).get("size").toString());
    }

    /**
     * hibernate查询结果(list<map>)) 转化为对象(list<obj>)
     *
     * @param list
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List queryListToObj(List list, Class<?> clazz) throws IllegalAccessException, InstantiationException {
        List resultList = new ArrayList<>();
        Object object = null;
        if (!CollectionUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                if (clazz == Integer.class || clazz == int.class) {
                    for (Object key : map.keySet()) {
                        object = Integer.parseInt(map.get(key).toString());
                    }
                } else if (clazz == Long.class || clazz == long.class) {
                    for (Object key : map.keySet()) {
                        object = Long.parseLong(map.get(key).toString());
                    }
                } else if (clazz == String.class) {
                    for (Object key : map.keySet()) {
                        object = map.get(key).toString();
                    }
                } else {
                    object = mapToObject(map, clazz);
                }
                resultList.add(object);
            }
        }

        return resultList;
    }

    /**
     * map转为对象
     *
     * @param mapValue
     * @param beanClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object mapToObject(Map<String, Object> mapValue, Class<?> beanClass) throws IllegalAccessException, InstantiationException {
        if (mapValue == null)
            return null;

        String filedName;
        Map<String, Object> map = new HashMap<>();
        for (String key : mapValue.keySet()) {
            Object obj = mapValue.get(key);
            filedName = underlineToCamel(key);
            map.put(key, obj);
            map.put(filedName, obj);
        }

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.get(field.getName()) instanceof BigInteger || map.get(field.getName()) instanceof BigDecimal) {
                if (field.getType() == Integer.class) {
                    field.set(obj, Integer.parseInt(map.get(field.getName()).toString()));
                } else if (field.getType() == Long.class) {
                    field.set(obj, Long.parseLong(map.get(field.getName()).toString()));
                } else if (field.getType() == Double.class) {
                    field.set(obj, Double.parseDouble(map.get(field.getName()).toString()));
                } else if (field.getType() == Float.class) {
                    field.set(obj, Float.parseFloat(map.get(field.getName()).toString()));
                } else {
                    field.set(obj, map.get(field.getName()));
                }
            } else {
                field.set(obj, map.get(field.getName()));
            }
        }

        return obj;
    }

    /**
     * 下划线字段转为驼峰字段
     *
     * @param param
     * @return
     */
    private static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
