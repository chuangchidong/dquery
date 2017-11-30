package com.free.dquery.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangzhidong
 * @date 2017/11/16
 */
public class PageInfo {
    public static String ASC  = "ASC";
    public static String DESC  = "DESC";

    int page = -1;
    int size = -1;
    List<Sort> sorts;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public List<Sort> add(Sort sort){
        if (sorts==null){
            sorts = new ArrayList<>();
        }
        sorts.add(sort);
        return sorts;
    }

    public List<Sort> add(List<Sort> sorts){
        if (sorts==null){
            sorts = new ArrayList<>();
        }
        this.sorts.addAll(sorts);
        return this.sorts;
    }

    public String getSorting(){
        String sorting = "";
        if (sorts!=null && sorts.size()>0){
            sorting = Arrays.toString(sorts.toArray());
            sorting = sorting.substring(1,sorting.length()-2);
        }

        return sorting;
    }

    public class Sort{
        String field;
        String sorting;

        public Sort() {
        }

        public Sort(String field, String sorting) {
            this.field = field;
            this.sorting = sorting;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getSorting() {
            return sorting;
        }

        public void setSorting(String sorting) {
            this.sorting = sorting;
        }

        @Override
        public String toString() {
            return field+" "+sorting;
        }
    }

}
