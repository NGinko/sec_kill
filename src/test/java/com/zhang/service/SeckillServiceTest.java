package com.zhang.service;

import com.zhang.domain.Seckill;
import com.zhang.dto.Exposer;
import com.zhang.dto.SeckillExecution;
import com.zhang.exception.RepeatKillException;
import com.zhang.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
        //System.out.println(list);
    }

    @Test
    public void getById() {
        long num = 1001;
        Seckill seckill = seckillService.getById(num);
        logger.info("list={}",seckill);




    }

    @Test
    public void exportSeckillUrl() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
    }

    @Test
    public void testSeckillLogic() throws  Exception {
        long id =1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer=()",exposer);
            long phone = 13669105589l;
            String md5 = exposer.getMd5();
            try{
                SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
                logger.info("execution={}",execution);
            }catch(RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillException e){
                logger.error(e.getMessage());
            }

        }else{
            logger.warn("exposer={}",exposer);
        }

    }

    @Test
    public void TestexecuteSeckillProcedure(){
        long seckillId = 1001;
        long phone = 13680115102L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info("execution={}", execution);
        }

    }
}