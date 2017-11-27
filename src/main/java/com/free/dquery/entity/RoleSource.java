package com.free.dquery.entity;

/**
 * @author zhangzhidong
 * @date 2017/11/13
 */
public class RoleSource {
    Integer roleId;
    String roleName;
    String resourceName;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
