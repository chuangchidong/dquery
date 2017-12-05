package com.free.dquery.component;

import com.alibaba.fastjson.JSON;
import com.free.dquery.dao.ITestDao;
import com.free.dquery.entity.RoleResourceEntity;
import com.free.dquery.entity.RoleSource;
import com.free.dquery.entity.RoleSourceDto;
import com.free.dquery.util.PageInfo;
import com.free.dquery.util.PageResult;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangzhidong
 * @date 2017/11/28
 */
@Component
public class TestComponent implements CommandLineRunner {


    @Resource
    ITestDao testDao;

    @Override
    public void run(String... strings) throws Exception {
//        RoleSourceDto dto = new RoleSourceDto();
//        dto.setRoleId(1);
////        dto.setResourceName("系统管理1");
//        dto.setRoleName("系统管理");
//
//
//        PageInfo pageInfo = new PageInfo();
//        pageInfo.setPage(1);
//        pageInfo.setSize(2);
//
//
//
//
////        RoleSource roleSource = testDao.findInfoJoin(dto);
////        System.out.println("结果===" + JSON.toJSONString(roleSource));
////        System.out.println("结果==="+ JSON.toJSONString(testDao.findIntegerList(dto)));
//        PageResult<RoleSource> pageResult = testDao.findPage(dto,pageInfo);
//        System.out.println("结果==="+ JSON.toJSONString(pageResult));

        RoleResourceEntity entity = new RoleResourceEntity();
        entity.setRoleId(1L);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setSize(2);
        pageInfo.setPage(1);


        PageResult<RoleResourceEntity> pageResult = testDao.findRolePage(entity,pageInfo);
        System.out.println("结果==="+ JSON.toJSONString(pageResult));



    }
}
