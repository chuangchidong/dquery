package com.free.dquery.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhidong
 * @date 2017/11/12
 */
public class PageResult<T> {

    Integer page;

    Integer size;

    Long total;

    List list;

    public <T> PageResult(Integer page, Integer size, Long total, List<?> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        if (list==null){
            this.list = new ArrayList<T>();
        }else {
            this.list = list;
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
