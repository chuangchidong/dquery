package com.free.dquery.queryparam;

/**
 * @author zhangzhidong
 * @date 2017/11/13
 */
public class QueryParam {
     private String key;
     private Object value;

    public QueryParam(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QueryParam{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
