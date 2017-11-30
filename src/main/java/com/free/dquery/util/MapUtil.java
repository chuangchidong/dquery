package com.free.dquery.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Map 的工具类
 * Created by DK on 2017/6/7.
 */
public class MapUtil {


    public static String mapToHttpRequestParam(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (StringUtils.isEmpty(sb.toString())) {
                sb.append(key + "=" + map.get(key).toString());
            } else {
                sb.append("&" + key + "=" + map.get(key).toString());
            }
        }

        return sb.toString();
    }

    public static <T> List<T> mapToObject(List<Map> list, Class c) throws InstantiationException, IllegalAccessException {
        List objectList = new ArrayList();
        for (Map map : list) {
            objectList.add(mapToObject(map, c));
        }
        return objectList;
    }

    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws IllegalAccessException, InstantiationException {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.get(field.getName()) instanceof BigInteger) {
                if (field.getType() == Integer.class) {
                    field.set(obj, Integer.parseInt(map.get(field.getName()).toString()));
                } else if (field.getType() == Long.class) {
                    field.set(obj, Long.parseLong(map.get(field.getName()).toString()));
                } else {
                    field.set(obj, map.get(field.getName()));
                }
            } else {
                field.set(obj, map.get(field.getName()));
            }
        }

        return obj;
    }


    private static <T> void setValue(Field field, T t, Map map) throws IllegalAccessException {
        if (map.get(field.getName()) == null) {
            field.set(t, null);
        }
        AnnotatedType annotatedType = field.getAnnotatedType();

        //Integet to Long   不能转换
        if ("java.lang.Long".equals(annotatedType.getType().getTypeName())) {
            field.set(t, Long.parseLong(map.get(field.getName()).toString()));
            return;
        }
        field.set(t, map.get(field.getName()));
    }

    public static void main(String[] args) {
        String sqlHead = "select b.role_id roleId,c.name roleName,a.name resourceName from sys_resource a left join sys_role_resource b on a.id=b.resource_id left join sys_role c on b.role_id=c.id where 1=1";
        String sqlCount = "select 1 ";

        String count = sqlHead.replaceFirst(sqlHead.substring(sqlHead.indexOf("select"), sqlHead.indexOf("from")), sqlCount);
        System.out.println(count);

    }
}
