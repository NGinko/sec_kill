package com.zhang.service;

import com.zhang.domain.Seckill;
import com.zhang.dto.Exposer;
import com.zhang.dto.SeckillExecution;
import com.zhang.exception.RepeatKillException;
import com.zhang.exception.SeckillCloseException;
import com.zhang.exception.SeckillException;

import java.util.List;

public interface SeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口的地址
     * 否则输出系统时间和秒杀开启时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
    throws SeckillException, RepeatKillException, SeckillCloseException;


    /**
     * 调用存储过程执行秒杀，不需要抛出异常
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return 根据返回结果抛出不同的实体信息
     */
    SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
    //不抛出异常是因为不在需要事务回滚，在数据库内部进行处理
}
