package com.free.dquery.dao;

import com.free.dquery.annotation.DQuery;
import com.free.dquery.annotation.DynamicSql;
import com.free.dquery.denum.DynamicSqlJudgmentType;
import com.free.dquery.entity.RoleSource;
import com.free.dquery.entity.RoleSourceDto;
import com.free.dquery.entity.Test;
import com.free.dquery.util.PageInfo;
import com.free.dquery.util.PageResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangzhidong
 * @date 2017/11/28
 */
@Repository
public interface ITestDao extends JpaRepository<Test, Long> {

    @DQuery(sqlHead = "select b.role_id roleId,c.name roleName,a.name resourceName from sys_resource a left join sys_role_resource b on a.id=b.resource_id left join sys_role c on b.role_id=c.id where 1=1 and c.id=:dto.roleId",
            dynamicSql = {
                    @DynamicSql(sql = " and a.name=:dto.resourceName", judgementField = "dto.resourceName", type = DynamicSqlJudgmentType.NOTEMPTY),
                    @DynamicSql(sql = " and a.name=:dto.roleName", judgementField = "dto.roleName", type = DynamicSqlJudgmentType.NOTEMPTY)
            }
    )
    RoleSource findInfoJoin(@Param("dto") RoleSourceDto dto);

    @DQuery(sqlHead = "select b.role_id roleId from sys_resource a left join sys_role_resource b on a.id=b.resource_id left join sys_role c on b.role_id=c.id where 1=1 and c.id=:dto.roleId")
    Long findIntegerList(@Param("dto") RoleSourceDto dto);


//    @DQuery(sqlHead = "select b.role_id roleId from sys_resource a left join sys_role_resource b on a.id=b.resource_id left join sys_role c on b.role_id=c.id where 1=1 and c.id=:dto.roleId",
//            dynamicSql = {
//                    @DynamicSql(sql = " and a.name=:dto.resourceName", judgementField = "dto.resourceName", type = DynamicSqlJudgmentType.NOTEMPTY),
//                    @DynamicSql(sql = " and a.name=:dto.roleName", judgementField = "dto.roleName", type = DynamicSqlJudgmentType.NOTEMPTY)
//            })
    @DQuery(sqlHead = "select b.role_id roleId from sys_resource a left join sys_role_resource b on a.id=b.resource_id left join sys_role c on b.role_id=c.id where 1=1 and c.id=:dto.roleId",
            dynamicSql = {
                    @DynamicSql(sql = " and a.name=:dto.resourceName", conditions = "dto.resourceName != null && dto.resourceName !='' "),
                    @DynamicSql(sql = " and a.name=:dto.roleName", conditions = "dto.roleName !=null && dto.roleName != '' ")
            })
    PageResult<RoleSource> findPage(@Param("dto") RoleSourceDto dto, @Param("page") PageInfo pageInfo);
}
