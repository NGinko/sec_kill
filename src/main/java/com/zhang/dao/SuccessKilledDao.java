package com.zhang.dao;

import com.zhang.domain.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {


    /**
     * 插入购买明细。可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 插入行数
     *
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);


    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象（该对象携带了seckill秒杀产品对象）
     * @param seckillId
     /* @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
