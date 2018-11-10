# dquery jpa动态SQL查询

* 1.实现在jpa项目中,根据查询结果直接返回结果对象,不需要按照entity的注解方式匹配字段
* 2.实现查询的参数动态变化
* 3.实现分页查询

  
* TestComponent.java 为使用案例
```java
 @DQuery(sqlHead = "select sum(a.payment_amount) total_amount,sum(refund_amount) refundTotalAmount, count(1) num,a.source,sum(a.profit) totalProfit, a.is_refund isRefund from t_order a where a.status=3",
            dynamicSql = {
                    @DynamicSql(sql = " and a.store_id in (:storeIds)", conditions = "storeIds !=null && storeIds != '' "),
                    @DynamicSql(sql = " and a.creation_time > :startTime ", conditions = "startTime !=null && startTime > 0 "),
                    @DynamicSql(sql = " and a.creation_time < :endTime ", conditions = "endTime !=null && endTime > 0 "),
                    @DynamicSql(sql = " and a.store_id = :storeId ", conditions = "storeId !=null && storeId > 0 ")
            },
            sqlTail = " group by a.source, a.is_refund")
    List<SaleStatisticsBaseDto> findSaleStatistics(@Param("storeIds") List<Long> storeIds, @Param("storeId") Long storeId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
```
#####
  DQuery 注解标识
  dynamicSql：扩展查询条件
  @DynamicSql sql为追加的查询条件，conditions为判断语句，为true则追加，false不追加
  返回值结果为普通的javabean对象，但是要保证与SQL中的字段名称一致

##### 对应的springboot starter包源码
https://github.com/chuangchidong/dquery-spring-boot-starter.git

项目下载编译，在自己的应用程序pom中加入引用，你懂得😉
```xml
     <dependency>
        <groupId>com.free.dquery</groupId>
      	<artifactId>dquery-spring-boot-starter</artifactId>
      	<version>0.0.1-SNAPSHOT</version>
    </dependency>
```


