package com.zhang.dao;

import com.zhang.domain.Seckill;
import com.zhang.domain.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {


    @Autowired
    private SeckillDao seckillDao;





    @Test
    public void queryById() {
        int number = 1000;
        Seckill seckill = seckillDao.queryById(number);
        System.out.println(seckill.getName());
        System.out.println(seckill);


    }


    @Test
    public void queryAll() {

        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() {
        Date date = new Date();
        int i = seckillDao.reduceNumber(1000l, date);
        System.out.println(i);
    }




}