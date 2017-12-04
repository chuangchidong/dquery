package com.free.dquery.util;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Map;

/**
 * Map 的工具类
 * Created by DK on 2017/6/7.
 */
public class MapUtil {

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

}
