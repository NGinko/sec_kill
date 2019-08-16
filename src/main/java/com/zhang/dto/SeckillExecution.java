package com.zhang.dto;

import com.zhang.domain.SuccessKilled;
import com.zhang.eunms.SeckillStateEnum;

/**
 * 封装秒杀执行结果
 */
public class SeckillExecution {

    private long seckillId;
    //秒杀执行结果的状态
    private int state;
    //状态表示
    private String stateInfo;
    //秒杀成功对象
    private SuccessKilled successKilled;

    //成功时构造方法

    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }


    //失败构造方法

    public SeckillExecution(long seckillId,  SeckillStateEnum seckillStateEnum) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state='" + state + '\'' +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
