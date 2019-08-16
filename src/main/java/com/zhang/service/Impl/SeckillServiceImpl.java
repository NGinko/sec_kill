package com.zhang.service.Impl;

import com.zhang.dao.SeckillDao;
import com.zhang.dao.SuccessKilledDao;
import com.zhang.dao.cache.RedisDao;
import com.zhang.domain.Seckill;
import com.zhang.domain.SuccessKilled;
import com.zhang.dto.Exposer;
import com.zhang.dto.SeckillExecution;
import com.zhang.eunms.SeckillStateEnum;
import com.zhang.exception.RepeatKillException;
import com.zhang.exception.SeckillCloseException;
import com.zhang.exception.SeckillException;
import com.zhang.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private final String slat = "asdkfuhfioj2jfoijpojwfpo2jpfojpo2;af2.';";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }


    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //利用缓存进行优化 再超时的基础上维护一致性
        //1.访问redis,尝试获取
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill==null){//2.缓存没有,则访问数据库

            seckill = seckillDao.queryById(seckillId);
            if(seckill == null){
                return new Exposer(false,seckillId);
            }else{
                //3.将查询结果放到数据库中
                redisDao.putSeckill(seckill);
            }

        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();

        //系统当时时间
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()||
        nowTime.getTime()> endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //秒杀开启，返回秒杀商品id ，加密接口的md5
        String md5= getMD5(seckillId);

        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckilledId){
        String base = seckilledId +"/" +slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return  md5;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = new Date();

        try{

            //减库存
            int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0){   //
                //没有更新到记录，秒杀结果 rollback
                throw new SeckillCloseException("seckill id close");
            }else {
                //减库存，商品之间有竞争
                int insetCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                //唯一seckillId,userPhone
                if (insetCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("throw repeated");
                } else {
                    //秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2) {
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage());
            //将所有编译器异常转换为运行期异常
            throw new SeckillException("Seckill inner erroe " + e.getMessage());
        }

    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(md5==null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程，result被复制
        seckillDao.killByProcedure(map);
        //执行result
        int result = MapUtils.getInteger(map, "result", -2);
        if(result == 1){
            SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
            return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
        }else{
            return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
        }
    }
}
