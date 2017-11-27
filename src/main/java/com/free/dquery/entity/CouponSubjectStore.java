package com.free.dquery.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 优惠券活动店铺
 *
 * @author zhangzhidong
 * @email zhangzhidong@beiquan.org
 * @date 2017-11-20 10:38:14
 */

@Entity
@Table(name = "t_coupon_subject_store")
public class CouponSubjectStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 20, nullable = false, columnDefinition = "BIGINT(20) NOT NULL COMMENT '主键ID'")
    private Long id;

    @Column(name = "subject_id", length = 20, nullable = false, columnDefinition = "BIGINT(20) NOT NULL COMMENT '优惠券活动ID'")
    private Long subjectId;

    @Column(name = "city_id", length = 20, nullable = false, columnDefinition = "BIGINT(20) NOT NULL COMMENT '城市id'")
    private Long cityId;

    @Column(name = "city_name", length = 64, nullable = true, columnDefinition = "VARCHAR(64) DEFAULT '' COMMENT '城市名称'")
    private String cityName;

    @Column(name = "store_id", length = 20, nullable = false, columnDefinition = "BIGINT(20) NOT NULL COMMENT '店铺ID'")
    private Long storeId;

    @Column(name = "store_name", length = 64, nullable = true, columnDefinition = "VARCHAR(64) DEFAULT '' COMMENT '店铺名称'")
    private String storeName;

    /**
     * 设置：主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置：优惠券活动ID
     */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * 获取：优惠券活动ID
     */
    public Long getSubjectId() {
        return subjectId;
    }

    /**
     * 设置：城市id
     */
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /**
     * 获取：城市id
     */
    public Long getCityId() {
        return cityId;
    }

    /**
     * 设置：城市名称
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * 获取：城市名称
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * 设置：店铺ID
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * 获取：店铺ID
     */
    public Long getStoreId() {
        return storeId;
    }

    /**
     * 设置：店铺名称
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * 获取：店铺名称
     */
    public String getStoreName() {
        return storeName;
    }
}
