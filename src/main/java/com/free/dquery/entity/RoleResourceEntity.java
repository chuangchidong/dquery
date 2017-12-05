package com.free.dquery.entity;

/**
 * @author zhangzhidong
 * @date 2017/12/4
 */
public class RoleResourceEntity {
    Long id;
    Long roleId;
    Long resourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
