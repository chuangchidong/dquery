package com.free.dquery.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.StringCodec;
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

    /**
     * 把sql 和参数封装到 query中去  注意 参数param 中的 object 只接受类型只接受 QueryParam 或者是 QueryParamList
     *
     * @param sql
     * @param param
     * @param session
     * @return
     */
    private static SQLQuery setSqlAndParam(String sql, List<Object> param, Session session) {
        System.err.println("SQL:" + sql);
        QueryParam queryParam;
        QueryParamList queryParamList;
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        //封装查询条件
        for (Object o : param) {
            if (o instanceof QueryParam) {
                queryParam = (QueryParam) o;
                sqlQuery.setParameter(queryParam.getKey(), queryParam.getValue());
                System.err.println(queryParam);
            }
            if (o instanceof QueryParamList) {
                queryParamList = (QueryParamList) o;
                sqlQuery.setParameterList(queryParamList.getKey(), queryParamList.getValue());
                System.err.println(queryParamList);
            }
        }
        return sqlQuery;
    }

    /**
     * 查询 不分页
     *
     * @param sql
     * @param param
     * @param session
     * @return
     */
    public static List<Map<String, Object>> queryNotPage(String sql, List<Object> param, Session session) {
        return queryPage(sql, param, null, null, session);
    }


    /**
     * 查询 不分页
     *
     * @param sql
     * @param param
     * @param sessionFactory
     * @return
     */
    public static List<Map<String, Object>> queryNotPage(String sql, List<Object> param, SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        List<Map<String, Object>> list = queryNotPage(sql, param, session);
        session.close();
        return list;
    }

    /**
     * 查询并且分页
     *
     * @param sql
     * @param param
     * @param page    当前页
     * @param size    每页条数
     * @param session
     * @return
     */
    public static List<Map<String, Object>> queryPage(String sql, List<Object> param, Integer page, Integer size, Session session) {
        SQLQuery sqlQuery = setSqlAndParam(sql, param, session);
        if (page != null && size != null) {
            sqlQuery.setFirstResult((page - 1) * size);
            sqlQuery.setMaxResults(size);//(这个数为几则要几条)
        }
        return sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }


    /**
     * 查询并且分页
     *
     * @param sql
     * @param param
     * @param page           当前页
     * @param size           每页条数
     * @param sessionFactory
     * @return
     */
    public static List<Map<String, Object>> queryPage(String sql, List<Object> param, Integer page, Integer size, SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        List<Map<String, Object>> list = queryPage(sql, param, page, size, session);
        session.close();
        return list;
    }

    /**
     * 查询并且分页
     *
     * @param sql
     * @param param
     * @param page           当前页
     * @param size           每页条数
     * @param sessionFactory
     * @return 返回实体
     */
    public static List queryPage(String sql, List<Object> param, Integer page, Integer size, Class c, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        List list = queryPage(sql, param, page, size, sessionFactory);
        if (c.newInstance() instanceof Void) {
            return list;
        }
        list = MapUtil.mapToObject(list, c);
        return list;
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






/*    ----------------------------------------------------------------------------------------   */

    public static List queryForList(String sql, List<Object> param, Integer page, Integer size, SessionFactory sessionFactory) throws IllegalAccessException, InstantiationException {
        Session session = sessionFactory.openSession();
        System.out.println("===sql===" + sql);
        System.out.println("===param===" + JSON.toJSONString(param));
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
            System.out.println("结果类型====" + list.get(0).getClass() + "结果值===:" + JSON.toJSONString(list.get(0)));
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

}
