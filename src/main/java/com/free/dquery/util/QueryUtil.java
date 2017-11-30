package com.free.dquery.util;

import com.free.dquery.queryparam.QueryParam;
import com.free.dquery.queryparam.QueryParamList;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhidong
 * @date 2017/11/14
 */
public class QueryUtil {

    public static List queryForList(String sql, List<Object> param, Integer page, Integer size, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        Session session = sessionFactory.openSession();
        SQLQuery sqlQuery = setQueryAndParam(sql, param, session);
        if (page != null && size != null) {
            sqlQuery.setFirstResult((page - 1) * size);
            sqlQuery.setMaxResults(page * size);//(这个数为几则要几条)
        }
        List list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        sqlQuery.getQueryString();
        session.close();

        return list;
    }

    public static Object queryForObject(String sql, List<Object> param, Class<?> clazz, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        Session session = sessionFactory.openSession();

        SQLQuery sqlQuery = setQueryAndParam(sql, param, session);

        List list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        session.close();
        Object object = null;
        if (list != null && list.size() > 0) {
            Map map = (Map) list.get(0);
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
                object = MapUtil.mapToObject(map, clazz);
            }
        }
        return object;
    }

    private static SQLQuery setQueryAndParam(String sql, List<Object> param, Session session) {
        QueryParam queryParam;
        QueryParamList queryParamList;
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        //封装查询条件
        for (Object o : param) {
            if (o instanceof QueryParam) {
                queryParam = (QueryParam) o;
                if (sql.contains(queryParam.getKey())) {
                    sqlQuery.setParameter(queryParam.getKey(), queryParam.getValue());
                }
            }
            if (o instanceof QueryParamList) {
                queryParamList = (QueryParamList) o;
                if (sql.contains(queryParamList.getKey())) {
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

        String sqlCount = sql.replaceFirst(sql.substring(sql.indexOf("SELECT"), sql.indexOf("FROM")), "SELECT 1 ");

        sqlCount = "select count(1) as size from (" + sqlCount + ") as t";

        Session session = sessionFactory.openSession();
        SQLQuery sqlQuery = setQueryAndParam(sqlCount, param, session);
        List<Map<String, Object>> list = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        session.close();
        return Long.parseLong(list.get(0).get("size").toString());
    }

}
