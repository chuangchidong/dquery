package com.free.dquery.component;

import com.alibaba.fastjson.JSON;
import com.free.dquery.dao.ITestDao;
import com.free.dquery.entity.RoleSource;
import com.free.dquery.entity.RoleSourceDto;
import com.free.dquery.util.PageInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
        RoleSourceDto dto = new RoleSourceDto();
        dto.setRoleId(1);
        dto.setResourceName("系统管理1");
//        dto.setRoleName("系统管理");


        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(1);
        pageInfo.setSize(2);




//        RoleSource roleSource = testDao.findInfoJoin(dto);
//        System.out.println("结果===" + JSON.toJSONString(roleSource));
//        System.out.println("结果==="+ JSON.toJSONString(testDao.findIntegerList(dto)));
        System.out.println("结果==="+ JSON.toJSONString(testDao.findPage(dto,pageInfo)));




    }
}
